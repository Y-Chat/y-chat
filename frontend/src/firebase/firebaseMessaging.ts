import {getMessaging, getToken, onMessage, isSupported} from "firebase/messaging";
import app from "./firebaseConfig";
import {showErrorNotification, showNotification} from "../notifications/notifications";
import {api, setApiAccessToken} from "../network/api";

const vapidKey = "BLkE7yXd0U01gJTC3sEDr3XYzlp4YZxKgNKyJEJyf2MipMm14IUNt-wK5JaSIcsFLBY7n8zhVcXTKXm4s7SvTYE";
const hasPermission = 'Notification' in window && Notification.permission == "granted"

// if permission already granted -> generate token
if (hasPermission) {
    generateToken();
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

    await generateToken();
    return true;
}

async function generateToken() {
    try {
        const messaging = getMessaging(app);

        let currentToken = await getToken(messaging, {vapidKey: vapidKey})

        if (!currentToken)
            console.log('No registration token available. Request permission to generate one.');

        console.log("FBC token: " + currentToken) // TODO remove
        setApiAccessToken(currentToken)

        onMessage(messaging, (payload) => {
            console.log('Received foreground message ', payload); //TODO remove
            showErrorNotification(payload.notification?.body || "", payload.notification?.title)
        })
    } catch (e) {
        console.log('An error occurred while retrieving token. ', e);
        return false;
    }
}
