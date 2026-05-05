package io.sovereign.collections.bridge;

import io.sovereign.collections.ImmutableList;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

/**
 * High-Performance Bridge for PCollections 5.0.0.
 * This bridge provides interoperability with PCollections without requiring a
 * compile-time dependency. It uses Method Handles for performance parity with
 * direct calls on modern JVMs (Java 21-25).
 */
public final class PCollectionsBridge {

    private static final MethodHandle EMPTY;
    private static final MethodHandle PLUS;

    static {
        MethodHandle emptyHandle = null;
        MethodHandle plusHandle = null;

        try {
            Class<?> pstackClass = Class.forName("org.pcollections.ConsPStack");
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            // PCollections 5.0.0+ Factory and Mutation API
            emptyHandle = lookup.findStatic(pstackClass, "empty",
                    MethodType.methodType(pstackClass));

            plusHandle = lookup.findVirtual(pstackClass, "plus",
                    MethodType.methodType(pstackClass, Object.class));

        } catch (Exception e) {
            // PCollections not found on classpath; bridge handles remain null.
            // Any attempt to use the bridge will result in an IllegalStateException.
        }

        EMPTY = emptyHandle;
        PLUS = plusHandle;
    }

    private PCollectionsBridge() {}

    /**
     * Converts ImmutableList to a PCollections PStack while preserving element order.
     * Use this when you need rich analytics or ecosystem compatibility.
     *
     * @throws IllegalStateException if PCollections is not in the classpath.
     */
    public static <T> Object toPStack(ImmutableList<T> list) {
        Objects.requireNonNull(list, "list cannot be null");

        if (EMPTY == null || PLUS == null) {
            throw new IllegalStateException(
                    "PCollections 5.0.0+ not found in classpath. " +
                            "Add dependency: org.pcollections:pcollections:5.0.0");
        }

        try {
            Object pstack = EMPTY.invoke();

            if (list.isEmpty()) {
                return pstack;
            }

            // We iterate in reverse because PStack is a linked structure (prepend-only).
            // This maintains the logical order of the Sovereign list.
            for (T item : list.reverse()) {
                pstack = PLUS.invoke(pstack, item);
            }

            return pstack;

        } catch (Throwable e) {
            throw new RuntimeException("Failed to convert ImmutableList to PStack", e);
        }
    }

    /**
     * Converts ImmutableList to PStack without preserving order (O(n)).
     * This is faster than toPStack() as it avoids the initial reverse iteration.
     */
    public static <T> Object toPStackReversed(ImmutableList<T> list) {
        Objects.requireNonNull(list, "list cannot be null");

        if (EMPTY == null || PLUS == null) {
            throw new IllegalStateException("PCollections 5.0.0+ not found in classpath");
        }

        try {
            Object pstack = EMPTY.invoke();
            for (T item : list) {
                pstack = PLUS.invoke(pstack, item);
            }
            return pstack;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to bridge to PStack in reversed mode", e);
        }
    }
}