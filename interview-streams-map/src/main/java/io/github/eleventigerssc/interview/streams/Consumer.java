package io.github.eleventigerssc.interview.streams;

/**
 * Copy of {@link java.util.function.Consumer} to be used within a {@link Stream}.
 */
interface Consumer<T> {

    void accept(T t);
}
