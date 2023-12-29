import {initializeApp} from "firebase/app";
import {getMessaging, getToken, onMessage} from "firebase/messaging";
import {getAuth} from "firebase/auth";

const firebaseConfig = {
    apiKey: "AIzaSyAQJb9j6HjvwapIo9OT_fNTGAxuIfNcGW8",
    authDomain: "y-chat-e5132.firebaseapp.com",
    projectId: "y-chat-e5132",
    storageBucket: "y-chat-e5132.appspot.com",
    messagingSenderId: "416841181268",
    appId: "1:416841181268:web:025b56230f33ed5f3ce5c1",
    measurementId: "G-TV16QZK2KS"
};

const app = initializeApp(firebaseConfig);

const messaging = getMessaging(app);
const auth = getAuth(app);

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

export {
    app,
    messaging,
    auth
}