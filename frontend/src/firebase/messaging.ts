import {getMessaging, getToken, onMessage, isSupported, MessagePayload} from "firebase/messaging";
import firebaseApp from "./firebaseApp";
import {showCallNotification, showErrorNotification, showNotification} from "../notifications/notifications";
import {api, setApiAccessToken} from "../network/api";

const vapidKey = "BLkE7yXd0U01gJTC3sEDr3XYzlp4YZxKgNKyJEJyf2MipMm14IUNt-wK5JaSIcsFLBY7n8zhVcXTKXm4s7SvTYE";
const hasPermission = 'Notification' in window && Notification.permission == "granted"

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
    await generateToken();
    setupNotificationHandler();

    registerNotificationTypeHandler("SIGNALING_NEW_OFFER", (payload: MessagePayload) => {
        console.log('Received SIGNALING_NEW_OFFER', payload);
        if(!payload || !payload.data) return;
        const callId = payload.data["call-id"];
        const callerId = payload.data["caller-id"];
        const offerSdp = payload.data["offer-sdp"];
        const offerType = payload.data["offer-type"];
        showCallNotification(callId, callerId);
    })

    registerNotificationTypeHandler("NEW_MESSAGE", (payload) => {
        showNotification(payload.notification?.body || "", payload.notification?.title)
    });

    registerNotificationTypeHandler(null, (payload) => {
        if (process.env.NODE_ENV === "development") {
        	console.log('Received foreground message ', payload);
        }
        showErrorNotification(payload.notification?.body || "", payload.notification?.title)
    });
}

const notificationTypeHandlers: { [type: string]:(payload: MessagePayload) => void; } = {}

export function registerNotificationTypeHandler(type: string | string[] | null, callback: (payload: MessagePayload) => void) {
    if(type === null) {
        notificationTypeHandlers["null"] = callback;
    } else if (typeof type === "string") {
        notificationTypeHandlers[type] = callback;
    } else {
        type.forEach(x => notificationTypeHandlers[x] = callback)
    }
}

export function unregisterNotificationTypeHandler(type: string | string[] | null) {
    if(type === null) {
        delete notificationTypeHandlers["null"]
    } else if (typeof type === "string") {
        delete notificationTypeHandlers[type]
    } else {
        type.forEach(x => delete notificationTypeHandlers[x])
    }
}

function setupNotificationHandler() {
    const messaging = getMessaging(firebaseApp);
    console.log("registering notification handler")
    onMessage(messaging, (payload) => {
        console.log("Received notification:", payload)
        if(!payload.data || !("type" in payload.data)) {
            notificationTypeHandlers["null"](payload);
            return;
        }
        const type = payload.data["type"]
        if(type in notificationTypeHandlers) {
            notificationTypeHandlers[type](payload);
            return;
        }

        console.error(`Received notification of type ${type} but no handler was registered`, payload)
    });
}

async function generateToken() {
    try {
        const messaging = getMessaging(firebaseApp);

        let currentToken = await getToken(messaging, {vapidKey: vapidKey})

        if (!currentToken)
            console.log('No registration token available. Request permission to generate one.');

        if (process.env.NODE_ENV === "development") {
            console.log("FBC token: " + currentToken);
        }
        setApiAccessToken(currentToken)
    } catch (e) {
        console.log('An error occurred while retrieving token. ', e);
        return false;
    }
}
