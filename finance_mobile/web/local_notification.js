// web/local_notification.js
function showNotification(title, body, payload) {
  if (!("Notification" in window)) {
    console.log("This browser does not support desktop notifications");
    return;
  }

  if (Notification.permission === "granted") {
    var notification = new Notification(title, {
      body: body,
      icon: "/icons/Icon-192.png"
    });

    if (payload && payload.length > 0) {
      notification.onclick = function() {
        window.open(payload, '_blank');
      };
    }
  } else if (Notification.permission !== "denied") {
    Notification.requestPermission().then(function(permission) {
      if (permission === "granted") {
        var notification = new Notification(title, {
          body: body,
          icon: "/icons/Icon-192.png"
        });

        if (payload && payload.length > 0) {
          notification.onclick = function() {
            window.open(payload, '_blank');
          };
        }
      }
    });
  }
}