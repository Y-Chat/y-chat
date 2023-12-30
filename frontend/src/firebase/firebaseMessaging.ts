import {getMessaging, getToken, onMessage} from "firebase/messaging";
import app from "./firebaseConfig";

const messaging = getMessaging(app);

Notification.requestPermission().then((permissions) => {
    if (permissions === "granted") {
        return getToken(messaging, {vapidKey: "BLkE7yXd0U01gJTC3sEDr3XYzlp4YZxKgNKyJEJyf2MipMm14IUNt-wK5JaSIcsFLBY7n8zhVcXTKXm4s7SvTYE"}).then((currentToken) => {
            if (currentToken) {
                console.log(currentToken)
                // Send the token to your server and update the UI if necessary
                // this identifies this running instance of the app to e.g. enable user-specific notifications
                // ...
            } else {
                // Show permission request UI
                console.log('No registration token available. Request permission to generate one.');
                // ...
            }
        }).catch((err) => {
            console.log('An error occurred while retrieving token. ', err);
            // ...
        });
    }
})

onMessage(messaging, (payload) => {
    console.log("message received ", payload)
})

export default messaging;