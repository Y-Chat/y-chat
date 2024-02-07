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

self.addEventListener('push', event => {
    const data = event.data;
    /*if (!data || !("type" in data)) {
        return;
    }*/
    event.waitUntil(
        // in here we pass showNotification, but if you pass a promise, like fetch,
        // then you should return showNotification inside of it. like above example.
        self.registration.showNotification("Hallo Freundeee", {
            body: "You received a new message!",
            tag: "YChat - New Message",
            icon: "https://y-chat.net/logo192.png",
            renotify: true,
            data: {
                url: "https://y-chat.net"
            }
        })
    );
});

self.addEventListener('notificationclick', function(event) {
    event.notification.close();
    event.waitUntil(
        clients.openWindow(event.data.url)
    );
})
