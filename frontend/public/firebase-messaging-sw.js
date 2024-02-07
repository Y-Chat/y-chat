// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here. Other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js');

// Initialize the Firebase app in the service worker by passing in
// your app's Firebase config object.
// https://firebase.google.com/docs/web/setup#config-object
firebase.initializeApp({
    apiKey: "AIzaSyAQJb9j6HjvwapIo9OT_fNTGAxuIfNcGW8",
    authDomain: "y-chat-e5132.firebaseapp.com",
    projectId: "y-chat-e5132",
    storageBucket: "y-chat-e5132.appspot.com",
    messagingSenderId: "416841181268",
    appId: "1:416841181268:web:025b56230f33ed5f3ce5c1",
    measurementId: "G-TV16QZK2KS"
});

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();

let newMessageCounter = 0;

async function checkClientIsVisible() {
    const windowClients = await clients.matchAll({
        type: "window",
        includeUncontrolled: true,
    });

    for (var i = 0; i < windowClients.length; i++) {
        if (windowClients[i].visibilityState === "visible") {
            return true;
        }
    }

    return false;
}

self.addEventListener('push', event => {
    const data = event.data;
    event.waitUntil(
        // in here we pass showNotification, but if you pass a promise, like fetch,
        // then you should return showNotification inside of it. like above example.
        checkClientIsVisible().then(async (isVisible) => {
              if(isVisible) {
                  event.preventDefault();
                  return;
              }
            newMessageCounter += 1;
            await self.registration.showNotification("New Message", {
                // body: JSON.stringify(event),
                body: `You have ${newMessageCounter} new message${newMessageCounter > 1 ? "s" : ""}`,
                tag: "YChat - New Message",
                icon: "https://y-chat.net/logo192.png",
                renotify: true,
                data: {
                  url: "https://y-chat.net"
                }
            })
        })
    );
});

self.addEventListener('notificationclick', function(event) {
    event.notification.close();
    newMessageCounter = 0;
    event.waitUntil(
        clients.openWindow(event.data.url)
    );
})
