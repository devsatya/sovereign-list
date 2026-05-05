package io.sovereign.collections.bridge;

import io.sovereign.collections.ImmutableList;
import java.util.Objects;
import java.util.Optional;

/**
 * Optional Bridge to Eclipse Collections.
 * Zero compile-time dependency.
 */
public final class EclipseCollectionsBridge {

    private static final boolean IS_AVAILABLE = checkAvailability();

    private EclipseCollectionsBridge() {}

    private static boolean checkAvailability() {
        try {
            Class.forName("org.eclipse.collections.api.list.ImmutableList",
                    false,
                    EclipseCollectionsBridge.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isAvailable() {
        return IS_AVAILABLE;
    }

    /**
     * Converts Sovereign ImmutableList → Eclipse Collections ImmutableList
     */
    public static <T> Optional<Object> toEclipseList(ImmutableList<T> list) {
        Objects.requireNonNull(list, "list cannot be null");

        if (!IS_AVAILABLE) {
            return Optional.empty();
        }

        try {
            // Use reflection to avoid compile-time dependency
            Class<?> fastListClass = Class.forName("org.eclipse.collections.impl.list.mutable.FastList");

            Object fastList = fastListClass.getMethod("newList", int.class)
                    .invoke(null, list.size());

            var addMethod = fastListClass.getMethod("add", Object.class);

            for (T item : list) {
                addMethod.invoke(fastList, item);
            }

            var toImmutableMethod = fastListClass.getMethod("toImmutable");
            return Optional.of(toImmutableMethod.invoke(fastList));

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Converts Eclipse Collections → Sovereign ImmutableList
     */
    @SuppressWarnings("unchecked")
    public static <T> ImmutableList<T> fromEclipseList(Object eclipseList) {
        Objects.requireNonNull(eclipseList, "eclipseList cannot be null");

        if (!(eclipseList instanceof Iterable<?> iterable)) {
            throw new IllegalArgumentException(
                    "Object is not an Eclipse Collections list: " + eclipseList.getClass().getName());
        }

        ImmutableList<T> result = ImmutableList.empty();

        for (Object item : iterable) {
            result = result.prepend((T) item);
        }

        return result.reverse();
    }
}