import {getMessaging, isSupported, MessagePayload, onMessage, Unsubscribe} from "firebase/messaging";
import firebaseApp from "./firebaseApp";
import {showCallNotification, showErrorNotification, showNotification} from "../notifications/notifications";
import {useMessagesStore} from "../state/messagesStore";

export const vapidKey = "BLkE7yXd0U01gJTC3sEDr3XYzlp4YZxKgNKyJEJyf2MipMm14IUNt-wK5JaSIcsFLBY7n8zhVcXTKXm4s7SvTYE";
const hasPermission = 'Notification' in window && Notification.permission === "granted"
const notificationTypeHandlers: { [type: string]: (payload: MessagePayload) => void; } = {}
let unsubscribeFromNotifications: Unsubscribe | null = null;

// if permission already granted -> generate token
if (hasPermission) {
    setupNotifications();
}

// request notification permissions and then generate token
export async function requestNotificationPermissions() {
    const supported = await isSupported();
    if (!supported || !('serviceWorker' in navigator)) {
        showNotification("Your browser is not support.", "Browser Not Supported")
        return false;
    }

    const permissions = await Notification.requestPermission();
    if (permissions !== "granted") {
        showNotification("Please give us permission to send notifications to your browser. This is necessary for the app to work properly.", "Notifications Blocked")
        return false;
    }

    await setupNotifications();
    return true;
}

async function setupNotifications() {
    setupNotificationHandler();

    registerNotificationTypeHandler("SIGNALING_NEW_OFFER", (payload: MessagePayload) => {
        console.log('Received SIGNALING_NEW_OFFER', payload);
        if (!payload || !payload.data) return;
        const callId = payload.data["call-id"];
        const callerId = payload.data["caller-id"];
        const offerSdp = payload.data["offer-sdp"];
        const offerType = payload.data["offer-type"];
        showCallNotification(callId, callerId, offerSdp, offerType);
    })

    registerNotificationTypeHandler("NEW_MESSAGE", (payload) => {
        if (!payload.data || !payload.data["chat-id"]) {
            return;
        }
        const chatId = payload.data["chat-id"]
        useMessagesStore.getState().fetchMoreMessagesByChat(chatId, "FUTURE", true);
        showNotification(payload.notification?.body || "", payload.notification?.title, `/chat/${chatId}`);
    });

    registerNotificationTypeHandler(null, (payload) => {
        if (process.env.NODE_ENV === "development") {
            console.log('Received foreground message ', payload);
        }
        showErrorNotification(payload.notification?.body || "", payload.notification?.title)
    });
}

export function registerNotificationTypeHandler(type: string | string[] | null, callback: (payload: MessagePayload) => void) {
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
}

function setupNotificationHandler() {
    const messaging = getMessaging(firebaseApp);
    console.log("registering notification handler")
    onMessage(messaging, (payload) => {
        console.log("Received notification:", payload)
        if (!payload.data || !("type" in payload.data)) {
            notificationTypeHandlers["null"](payload);
            return;
        }
        const type = payload.data["type"]
        if (type in notificationTypeHandlers) {
            notificationTypeHandlers[type](payload);
            return;
        }

        console.error(`Received notification of type ${type} but no handler was registered`, payload)
    });
}
