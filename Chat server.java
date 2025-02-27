import java.io.*;
import java.net.*;
import java.util.*;
import javax.websocket.*;
import javax.websocket.server.*;
import nl.martijndwars.webpush.*;

@ServerEndpoint("/chat")
public class ChatServer {
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static List<Subscription> subscriptions = new ArrayList<>();
    
    private static final String VAPID_PUBLIC_KEY = "YOUR_PUBLIC_VAPID_KEY";
    private static final String VAPID_PRIVATE_KEY = "YOUR_PRIVATE_VAPID_KEY";

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("Received: " + message);
        
        // Broadcast message to all clients
        for (Session s : sessions) {
            s.getBasicRemote().sendText(message);
        }

        // Send push notification
        sendPushNotification(message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    // Store push subscription
    public static void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    // Send push notification
    private static void sendPushNotification(String message) {
        for (Subscription sub : subscriptions) {
            try {
                Notification notification = new Notification(
                        sub, "{\"title\": \"New Chat Message\", \"body\": \"" + message + "\"}", VAPID_PUBLIC_KEY, VAPID_PRIVATE_KEY
                );
                PushService pushService = new PushService();
                pushService.send(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
