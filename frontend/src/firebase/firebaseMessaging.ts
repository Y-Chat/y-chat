import {getMessaging, getToken, onMessage} from "firebase/messaging";
import app from "./firebaseConfig";
import {showErrorNotification, showNotification} from "../notifications/notifications";

const messaging = getMessaging(app);

// if browser does not support service workers, FCM will not work at all
if ('serviceWorker' in navigator) {
    Notification.requestPermission().then((permissions) => {
        if (permissions !== "granted") {
            showNotification("You will not receive notifications, if the app is closed.", "Background Notifications Disabled")
        }
    })

    getToken(messaging, {vapidKey: "BLkE7yXd0U01gJTC3sEDr3XYzlp4YZxKgNKyJEJyf2MipMm14IUNt-wK5JaSIcsFLBY7n8zhVcXTKXm4s7SvTYE"}).then((currentToken) => {
        if (currentToken) {
            console.log(currentToken)
            // Send the token to your server and update the UI if necessary
            // this identifies this running instance of the app to e.g. enable user-specific notifications
        } else {
            console.log('No registration token available. Request permission to generate one.');
        }
    }).catch((err) => {
        console.log('An error occurred while retrieving token. ', err);
    });

    onMessage(messaging, (payload) => {
        console.log('Received foreground message ', payload); //TODO remove
        showErrorNotification(payload.notification?.body || "", payload.notification?.title)
    })
}

export default messaging;
