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

messaging.onBackgroundMessage((payload) => {
    if (!payload.data || !payload.data["chat-id"]) {
        return;
    }
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body,
        icon: payload.notification.image
    };
    const chatId = payload.data["chat-id"]
    const channel4Broadcast = new BroadcastChannel('channel4');
    channel4Broadcast.postMessage({key: chatId});

    const chats = JSON.parse(localStorage.get("offline-updates"));
    localStorage.setItem('offline-updates', [chatId, ...chats]);

    navigator.serviceWorker.controller.postMessage({
        type: 'CHAT_UPDATE',
        chatId: chatId
    });
    self.registration.showNotification(notificationTitle, notificationOptions);
});