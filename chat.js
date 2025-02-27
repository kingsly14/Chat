const socket = new WebSocket("ws://localhost:8080/chat");

// Get elements
const chatDiv = document.getElementById("chat");
const messageInput = document.getElementById("message");
const sendButton = document.getElementById("send");
const subscribeButton = document.getElementById("subscribe");

// Send message
sendButton.addEventListener("click", () => {
    const message = messageInput.value;
    if (message.trim()) {
        socket.send(message);
        messageInput.value = "";

        // Send push notification request
        fetch("/push/send", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message }),
        });
    }
});

// Receive messages
socket.onmessage = (event) => {
    const messageElement = document.createElement("p");
    messageElement.textContent = event.data;
    chatDiv.appendChild(messageElement);
};


socket.onmessage = (event) => {
    const messageElement = document.createElement("p");
    messageElement.textContent = event.data;
    chatDiv.appendChild(messageElement);

    // 🔔 Show in-app notification instead of browser notification
    showNotification("New Message", event.data);
};

// In-App Notification Function
function showNotification(title, message) {
    const notificationDiv = document.createElement("div");
    notificationDiv.style.position = "fixed";
    notificationDiv.style.bottom = "20px";
    notificationDiv.style.right = "20px";
    notificationDiv.style.background = "blue";
    notificationDiv.style.color = "white";
    notificationDiv.style.padding = "10px";
    notificationDiv.style.borderRadius = "5px";
    notificationDiv.innerHTML = `<strong>${title}</strong><br>${message}`;

    document.body.appendChild(notificationDiv);
    setTimeout(() => notificationDiv.remove(), 5000);
}

// Push Notification Subscription
subscribeButton.addEventListener("click", () => {
    if ("serviceWorker" in navigator && "PushManager" in window) {
        navigator.serviceWorker.register("sw.js").then(reg => {
            reg.pushManager.subscribe({
                userVisibleOnly: true,
                applicationServerKey: "YOUR_PUBLIC_VAPID_KEY"
            }).then(sub => {
                fetch("/push/subscribe", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(sub),
                }).then(() => alert("Subscribed for notifications"));
            });
        });
    }
});
