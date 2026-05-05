package example;

import io.sovereign.collections.ImmutableList; // This is YOUR lib
import io.sovereign.collections.bridge.EclipseCollectionsBridge;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import java.time.LocalDateTime;

public class AdvancedEventAnalytics {
    public static void main(String[] args) {
        // 1. DATA INGESTION (Uses Your Lib)
        ImmutableList<UserEvents> events = ImmutableList.of(
                new UserEvents("user-101", "LOGIN", LocalDateTime.now(), "EU")
        );

        if (!EclipseCollectionsBridge.isAvailable()) return;

        // 2. THE BURST (Bridge to Eclipse)
        // We use the full path here to avoid the import conflict
        org.eclipse.collections.api.list.ImmutableList<UserEvents> richList =
                EclipseCollectionsBridge.toEclipseList(events)
                        .map(obj -> (org.eclipse.collections.api.list.ImmutableList<UserEvents>) obj)
                        .orElseThrow();

        // 3. ANALYTICS (Uses Eclipse)
        ImmutableListMultimap<String, UserEvents> eventsByUser = richList.groupBy(UserEvents::userId);

        // 4. RESTORING SOVEREIGNTY (Back to Your Lib)
        ImmutableList<UserEvents> backToSovereign = EclipseCollectionsBridge.fromEclipseList(richList);

        System.out.println("Final Sovereign List Size: " + backToSovereign.size());
    }

    public record UserEvents(String userId, String action, LocalDateTime timestamp, String region) {}
}