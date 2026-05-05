package io.sovereign.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Truly Immutable Persistent Linked List using Sealed Interface + Records.
 *
 * Functional, persistent, thread-safe singly-linked list with structural sharing.
 * Inspired by Haskell, Scala, and Clojure.
 */
public sealed interface ImmutableList<T> extends Iterable<T>
        permits ImmutableList.Nil, ImmutableList.Cons {

    record Nil<T>() implements ImmutableList<T> {
        @Override
        public String toString() {
            return "[]";
        }
    }

    record Cons<T>(T head, ImmutableList<T> tail) implements ImmutableList<T> {
        @Override
        public String toString() {
            return "[" + head + toStringWithComma(tail) + "]";
        }
    }

    // ====================== Factory Methods ======================

    static <T> ImmutableList<T> empty() {
        return new Nil<>();
    }

    static <T> ImmutableList<T> of(T value) {
        return new Cons<>(value, empty());
    }

    @SafeVarargs
    static <T> ImmutableList<T> of(T... values) {
        ImmutableList<T> list = empty();
        for (int i = values.length - 1; i >= 0; i--) {
            list = list.prepend(values[i]);
        }
        return list;
    }

    // ====================== Core Operations ======================

    default ImmutableList<T> prepend(T value) {
        return new Cons<>(value, this);
    }

    // ====================== Utility Methods ======================

    default boolean isEmpty() {
        return this instanceof Nil;
    }

    default T head() {
        return switch (this) {
            case Cons(var h, _) -> h;
            case Nil() -> throw new NoSuchElementException("head of empty list");
        };
    }

    default ImmutableList<T> tail() {
        return switch (this) {
            case Cons(_, var t) -> t;
            case Nil() -> throw new NoSuchElementException("tail of empty list");
        };
    }

    default int size() {
        int count = 0;
        for (ImmutableList<T> curr = this; curr instanceof Cons<T> cons; curr = cons.tail) {
            count++;
        }
        return count;
    }

    // ====================== Functional Operations ======================

    default <R> ImmutableList<R> map(Function<T, R> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        ImmutableList<R> result = empty();
        ImmutableList<T> current = this;

        while (current instanceof Cons<T> cons) {
            result = result.prepend(mapper.apply(cons.head));
            current = cons.tail;
        }
        return result.reverse();
    }

    default ImmutableList<T> filter(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate cannot be null");

        ImmutableList<T> result = empty();
        ImmutableList<T> current = this;

        while (current instanceof Cons<T> cons) {
            if (predicate.test(cons.head)) {
                result = result.prepend(cons.head);
            }
            current = cons.tail;
        }
        return result.reverse();
    }

    // ====================== Reverse ======================

    default ImmutableList<T> reverse() {
        return reverseHelper(this, empty());
    }

    private static <T> ImmutableList<T> reverseHelper(ImmutableList<T> current, ImmutableList<T> acc) {
        return switch (current) {
            case Nil() -> acc;
            case Cons(var h, var t) -> reverseHelper(t, acc.prepend(h));
        };
    }

    // ====================== String Representation ======================

    private static String toStringWithComma(ImmutableList<?> list) {
        return switch (list) {
            case Nil() -> "";
            case Cons(var h, var t) -> ", " + h + toStringWithComma(t);
        };
    }

    // ====================== Traversal ======================

    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action cannot be null");

        ImmutableList<T> current = this;
        while (current instanceof Cons<T> cons) {
            action.accept(cons.head);
            current = cons.tail;
        }
    }

    // ====================== Iterable Support ======================

    @Override
    default Iterator<T> iterator() {
        return new Iterator<>() {
            private ImmutableList<T> current = ImmutableList.this;

            @Override
            public boolean hasNext() {
                return current instanceof Cons;
            }

            @Override
            public T next() {
                if (!(current instanceof Cons<T> cons)) {
                    throw new NoSuchElementException();
                }
                T value = cons.head;
                current = cons.tail;
                return value;
            }
        };
    }
}