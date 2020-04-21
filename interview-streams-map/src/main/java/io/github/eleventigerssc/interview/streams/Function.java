package io.github.eleventigerssc.interview.streams;

/**
 * Copy of {@link java.util.function.Function} to be used within a {@link Stream}.
 */
interface Function<T, R> {

    R call(T t);
}
