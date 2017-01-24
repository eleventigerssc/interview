package io.github.eleventigerssc.interview.streams;

import java.util.Iterator;

interface Stream<T> {

    /**
     * The element iterable for this stream
     */
    Iterator<T> iterator();

    /**
     * Performs an action for each element of this stream.
     */
    void forEach(Consumer<? super T> action);

    /**
     * Returns a stream consisting of the results of applying the given function to the elements of this stream.
     */
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    /**
     * Returns a stream consisting of the results of replacing each element of this stream with the contents
     * of a mapped stream produced by applying the provided mapping function to each element.
     */
    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    /**
     * Returns a stream consisting of the elements of this stream that match the given predicate.
     */
    Stream<T> filter(Predicate<? super T> predicate);
}
