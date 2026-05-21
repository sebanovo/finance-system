importScripts('https://www.gstatic.com/firebasejs/12.13.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/12.13.0/firebase-messaging-compat.js');

// ✅ Tu configuración de Firebase (misma que en index.html)
firebase.initializeApp({
  apiKey: "AIzaSyCZPmAnJnsPqzlhi86ZxJvM-vpaNQ7IY4c",
  authDomain: "finance-system-29d3d.firebaseapp.com",
  projectId: "finance-system-29d3d",
  storageBucket: "finance-system-29d3d.firebasestorage.app",
  messagingSenderId: "788345017826",
  appId: "1:788345017826:web:6e00ae6fdc2238488cd5bb",
  measurementId: "G-XEK7JX11GH"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  console.log('[firebase-messaging-sw.js] Mensaje en segundo plano:', payload);
  
  const notificationTitle = payload.notification?.title || 'Nueva notificación';
  const notificationOptions = {
    body: payload.notification?.body || '',
    icon: '/icons/Icon-192.png',
    badge: '/icons/Icon-192.png',
    data: payload.data || {},
  };
  
  return self.registration.showNotification(notificationTitle, notificationOptions);
});