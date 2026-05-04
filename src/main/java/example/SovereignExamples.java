package example;

import io.sovereign.collections.ImmutableList;
import java.time.Instant;

public class SovereignExamples {

    /**
     * A complex record with validation, multiple fields, and a custom method.
     * This represents a real-world "Health Check" system.
     */
    public record HealthCheck(
            String serviceId,
            int statusCode,
            long latencyMs,
            Instant timestamp,
            String environment
    ) {
        // Compact Constructor for Validation
        public HealthCheck {
            if (statusCode < 100 || statusCode > 599) {
                throw new IllegalArgumentException("Invalid HTTP status code: " + statusCode);
            }
        }

        // Derived property (logic inside the record)
        public boolean isCritical() {
            return statusCode >= 500 || latencyMs > 2000;
        }
    }

    public static void main(String[] args) {
        // Creating a list of complex records
        var checks = ImmutableList.of(
                new HealthCheck("auth-api", 200, 150, Instant.now(), "prod"),
                new HealthCheck("db-cluster", 503, 4500, Instant.now(), "prod"),
                new HealthCheck("search-index", 200, 2200, Instant.now(), "staging")
        );

        // 1. Filtering: Find only critical failures
        var criticalIssues = checks.filter(HealthCheck::isCritical);

        // 2. Mapping: Create alert messages for the critical issues
        var alerts = criticalIssues.map(hc ->
                String.format("[ALERT] %s in %s is failing with code %d",
                        hc.serviceId(), hc.environment(), hc.statusCode())
        );

        System.out.println("Critical alerts generated: " + alerts.size());
        alerts.forEach(System.out::println);
    }
}