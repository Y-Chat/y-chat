import { Unsubscribe } from "firebase/messaging";
import firebaseApp from "./firebaseApp";
import {showCallNotification, showNotification} from "../notifications/notifications";
import { getFirestore, onSnapshot, collection , deleteDoc, query, orderBy, startAfter, limit, documentId, getDocs } from "firebase/firestore";
import {useMessagesStore} from "../state/messagesStore";
import {useChatsStore} from "../state/chatsStore";
import {NavigateFunction} from "react-router-dom";

export type Notification = {
    type: "NEW_MESSAGE" | "SIGNALING_NEW_OFFER" | "SIGNALING_NEW_ANSWER" | "SIGNALING_NEW_CANDIDATE" | "CALL_ENDED" | undefined,

    chatId: string | undefined,

    offerSdp: string | undefined,
    offerType: string | undefined,
    callId: string | undefined,
    callerId: string | undefined,

    answerSdp: string | undefined,
    answerType: string | undefined,
    calleeId: string | undefined,

    candidateCandidate: string | undefined,
    candidateSdpMid: string | undefined,
    candidateUsernameFragment: string | undefined,
    candidateSdpMLineIndex: string | undefined
}

export const vapidKey = "BLkE7yXd0U01gJTC3sEDr3XYzlp4YZxKgNKyJEJyf2MipMm14IUNt-wK5JaSIcsFLBY7n8zhVcXTKXm4s7SvTYE";
const hasPermission = 'Notification' in window && Notification.permission === "granted"
const notificationTypeHandlers: { [type: string]: (payload: Notification) => void; } = {}
let unsubscribeFromNotifications: Unsubscribe | null = null;

// if permission already granted -> generate token
if (hasPermission) {
    setupNotifications();
} else {
    requestNotificationPermissions()
}

// request notification permissions and then generate token
export async function requestNotificationPermissions() {
    showNotification(
        "Please give us permission to send notifications to your browser. This is necessary to receive chat messages when YChat is closed",
        "Offline Notification Permissions",
        {autoClose: false, action: {text: "Grant permission", callback: async () => {
            console.log("Requesting Permission from browser...")
            const status = await Notification.requestPermission();
            console.log("status", status)
            await setupNotifications();
        }}}
    )
}

async function setupNotifications() {
    setupServiceFirebaseCloudMessaging();

    registerNotificationTypeHandler("SIGNALING_NEW_OFFER", (payload: Notification) => {
        console.log('Received SIGNALING_NEW_OFFER', payload);
        if (!payload || !payload.callId || !payload.callerId || !payload.offerSdp || !payload.offerType) return;
        showCallNotification(payload.callId, payload.callerId, payload.offerSdp, payload.offerType);
    })

    registerNotificationTypeHandler("NEW_MESSAGE", async (payload) => {
        if (!payload || !payload.chatId) {
            return;
        }
        await useMessagesStore.getState().fetchMoreMessagesByChat(payload.chatId, "FUTURE", true);
        if(window.location.pathname !== `/chat/${payload.chatId}`) {
            const chat = await useChatsStore.getState().getChat(payload.chatId)
            showNotification(chat?.lastMessage ? chat.lastMessage : "You received a new message", chat ? chat.name : "New Message", {action: {link: `/chat/${payload.chatId}`, text: "To the chat"}, sound: "/new_message.mp3"});
        } else {
            const audio = new Audio("/new_message.mp3")
            audio.volume = 0.1
            audio.play().catch(() => {
                console.error("can't start notification audio autoplay, because it's being blocked by the browser")
            })
        }
    });
}

export function registerNotificationTypeHandler(type: string | string[] | null, callback: (payload: Notification) => void) {
    if (type === null) {
        notificationTypeHandlers["null"] = callback;
    } else if (typeof type === "string") {
        notificationTypeHandlers[type] = callback;
    } else {
        type.forEach(x => notificationTypeHandlers[x] = callback)
    }
}

export function unregisterNotificationTypeHandler(type: string | string[] | null) {
    if (type === null) {
        delete notificationTypeHandlers["null"]
    } else if (typeof type === "string") {
        delete notificationTypeHandlers[type]
    } else {
        type.forEach(x => delete notificationTypeHandlers[x])
    }
}

export function unsubscribeNotifications() {
    if(!unsubscribeFromNotifications) return;
    unsubscribeFromNotifications();
    unsubscribeFromNotifications = null;
}

// Used to store information that can be used after all startup in app notifications have been handled
// Is being used in handleStartupInAppNotificationFinished
type HandleStartupInAppNotificationAggregate = {
    chatsWithNewMessages: Set<string>
}

function handleStartupInAppNotification(notification: Notification, aggregate: HandleStartupInAppNotificationAggregate): HandleStartupInAppNotificationAggregate {
    if(notification.type === "NEW_MESSAGE" && notification.chatId) {
        aggregate.chatsWithNewMessages.add(notification.chatId)
    }
    return aggregate;
}

function handleStartupInAppNotificationFinished(aggregate: HandleStartupInAppNotificationAggregate) {
    aggregate.chatsWithNewMessages.forEach((chatId) => {
        useMessagesStore.getState().fetchMoreMessagesByChat(chatId, "FUTURE", true);
    })
}

export function setupNotificationHandler(userUid: string) {
    const firestore = getFirestore(firebaseApp);
    console.log("registering notification handler")
    if(unsubscribeFromNotifications) {
        unsubscribeFromNotifications();
        unsubscribeFromNotifications = null;
    }

    // Querying (with pagination) for all new notifications that were sent while app wasn't open and handling them in handleStartupInAppNotification
    // Then Setting up live notification listener and handling them with callbacks which are registered with registerNotificationTypeHandler
    const notificationCollection = collection(firestore, "users", userUid, "notifications");
    const pageSize = 15;
    const notificationPaginatedQuery = query(notificationCollection, orderBy(documentId()), limit(pageSize))
    getDocs(notificationPaginatedQuery).then((snapshots) => {
        let aggregate: HandleStartupInAppNotificationAggregate = {chatsWithNewMessages: new Set()}
        snapshots.forEach((x) => {
            aggregate = handleStartupInAppNotification(x.data() as Notification, aggregate);
            deleteDoc(x.ref)
        })
        return {lastHandled: snapshots.docs.length > 0 ? snapshots.docs[snapshots.docs.length - 1] : null, lastPageSize: snapshots.docs.length, aggregate: aggregate};
    }).then(async ({lastHandled, lastPageSize, aggregate}) => {
        while(lastHandled !== null && lastPageSize >= pageSize) {
            await getDocs(query(notificationPaginatedQuery, startAfter())).then((snapshots) => {
                snapshots.forEach((x) => {
                    aggregate = handleStartupInAppNotification(x.data() as Notification, aggregate);
                    deleteDoc(x.ref)
                })
                lastHandled= snapshots.docs.length > 0 ? snapshots.docs[snapshots.docs.length - 1] : null
                lastPageSize= snapshots.docs.length
            })
        }
        handleStartupInAppNotificationFinished(aggregate);
    }).catch((err) => {
        console.error("Error processing in app notification that were sent while offline", err)
    }).then(() => {
        unsubscribeFromNotifications = onSnapshot(notificationCollection, (snapshot) => {
            snapshot.docChanges().map(async (change) => {
                if(change.type !== "added") return;
                const data = change.doc.data() as Notification;
                if(!data.type) {
                    if("null" in notificationTypeHandlers) {
                        notificationTypeHandlers["null"](data);
                        deleteDoc(change.doc.ref)
                    }
                    return;
                }
                if(data.type in notificationTypeHandlers) {
                    notificationTypeHandlers[data.type](data);
                    deleteDoc(change.doc.ref)
                    return;
                }

                console.error(`Received notification of type ${data.type} but no handler was registered`, data)
            })
        })
    }).catch((err) => {
        console.error("Error setting up live notification listener", err)
    })
}

function setupServiceFirebaseCloudMessaging() {
    navigator.serviceWorker.register('/firebase-messaging-sw.js', {scope: "/firebase-cloud-messaging-push-scope"})
}
