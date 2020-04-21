package io.github.eleventigerssc.interview.streams;

/**
 * Copy of {@link java.util.function.Predicate} to be used within a {@link Stream}.
 */
interface Predicate<T> {

    boolean test(T t);
}
