package example;

import io.sovereign.collections.ImmutableList;

public class Playground {

    // A custom record to demonstrate complex types
    public record ServerMetric(String hostname, double cpuUsage, int activeConnections) {}

    public static void main(String[] args) {

        // --- Example 1: Integers ---
        ImmutableList<Integer> scores = ImmutableList.of(95, 88, 72);
        var higherScores = scores.prepend(100);

        System.out.println("Integer List: " + higherScores);

        // --- Example 2: Custom Objects (SRE Metrics) ---
        var m1 = new ServerMetric("prod-db-01", 45.2, 120);
        var m2 = new ServerMetric("prod-web-04", 12.5, 300);

        ImmutableList<ServerMetric> metrics = ImmutableList.of(m1, m2);

        // Filter metrics where CPU is high
        var alertMetrics = metrics.filter(m -> m.cpuUsage() > 40.0);

        System.out.println("High CPU Alerts count: " + alertMetrics.size());

        // Transform metrics to just hostnames
        var hostnames = metrics.map(ServerMetric::hostname);
        System.out.println("Monitoring Hosts: " + hostnames);
    }
}