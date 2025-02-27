import nl.martijndwars.webpush.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.*;
import java.util.*;

@RestController
@RequestMapping("/push")
public class PushNotificationController {
    private final List<Subscription> subscriptions = new ArrayList<>();

    private static final String VAPID_PUBLIC_KEY = "YOUR_PUBLIC_VAPID_KEY";
    private static final String VAPID_PRIVATE_KEY = "YOUR_PRIVATE_VAPID_KEY";

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody Subscription subscription) {
        subscriptions.add(subscription);
        return ResponseEntity.ok("Subscribed");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> payload) throws GeneralSecurityException {
        PushService pushService = new PushService(VAPID_PUBLIC_KEY, VAPID_PRIVATE_KEY);
        for (Subscription sub : subscriptions) {
            Notification notification = new Notification(
                    sub, "{\"title\": \"New Chat Message\", \"body\": \"" + payload.get("message") + "\"}", VAPID_PUBLIC_KEY, VAPID_PRIVATE_KEY
            );
            pushService.send(notification);
        }
        return ResponseEntity.ok("Notification Sent");
    }
}
