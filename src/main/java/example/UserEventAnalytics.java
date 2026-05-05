package example;

import io.sovereign.collections.ImmutableList;
import io.sovereign.collections.bridge.PCollectionsBridge;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;

import java.util.Comparator;

/**
 * Demonstrates how to use Sovereign ImmutableList with PCollections Bridge
 * for Analytics / Big Data use cases.
 */
public class UserEventAnalytics {

    public static void main(String[] args) {

        // 1. Sovereign ImmutableList - Core Data Structure
        ImmutableList<UserEvent> events = ImmutableList.of(
                new UserEvent("user-123", "LOGIN", "2025-05-05T10:00"),
                new UserEvent("user-123", "VIEW_REPORT", "2025-05-05T10:05"),
                new UserEvent("user-123", "EXPORT_DATA", "2025-05-05T10:10")
        );

        // 2. Create new version efficiently (O(1) + structural sharing)
        ImmutableList<UserEvent> eventsV2 = events.prepend(
                new UserEvent("user-123", "LOGOUT", "2025-05-05T10:15")
        );

        // 3. Bridge to PCollections for rich API
        @SuppressWarnings("unchecked")
        PStack<UserEvent> richEvents = (PStack<UserEvent>) PCollectionsBridge.toPStack(eventsV2);

        // 4. Rich Analytics Operations using PCollections
        PStack<UserEvent> exportEvents = ConsPStack.from(
                richEvents.stream()
                        .filter(e -> "EXPORT_DATA".equals(e.action()))
                        .toList()
        );

        PStack<UserEvent> sortedEvents = ConsPStack.from(
                richEvents.stream()
                        .sorted(Comparator.comparing(UserEvent::timestamp))
                        .toList()
        );

        // Results
        System.out.println("Total Events (Sovereign)     : " + eventsV2.size());
        System.out.println("Total Events (PStack)        : " + richEvents.size());
        System.out.println("Export Events                : " + exportEvents.size());
        System.out.println("Latest Event                 : " + richEvents.getFirst().action());
        System.out.println("First Event in sorted list   : " + sortedEvents.getFirst().action());
    }
}

/**
 * Simple immutable domain event
 */
record UserEvent(String userId, String action, String timestamp) {}