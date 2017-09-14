package io.github.eleventigerssc.interview.streams;

import java.util.Iterator;

/**
 * @author Alexey
 * @since 9/14/17
 */
final class StreamImpl<T> implements Stream<T> {

    private final Iterable<T> source;

    StreamImpl(Iterable<T> source) {
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return source.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        final Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.next());
        }
    }

    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
        return new StreamImpl<>(new FilteredIterable<>(source, predicate));
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        return new StreamImpl<>(new MapIterable<>(source, mapper));
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new StreamImpl<>(new FlatMapIterable<>(source, mapper));
    }

    private static class FilteredIterable<T> implements Iterable<T> {

        private final Iterable<T> origin;
        private final Predicate<? super T> filtrationCondition;
        private T currentElement;

        public FilteredIterable(Iterable<T> origin, Predicate<? super T> filtrationCondition) {
            this.origin = origin;
            this.filtrationCondition = filtrationCondition;
        }

        @Override
        public Iterator<T> iterator() {
            final Iterator<T> originIterator = origin.iterator();
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    if (originIterator.hasNext()) {
                        currentElement = originIterator.next();
                        return filtrationCondition.test(currentElement);
                    } else {
                        return false;
                    }
                }

                @Override
                public T next() {
                    return currentElement;
                }
            };
        }
    }

    private static class MapIterable<I, O> implements Iterable<O> {

        private final Iterable<I> origin;
        private final Function<? super I, ? extends O> mapper;

        @Override
        public Iterator<O> iterator() {
            final Iterator<I> originIterator = origin.iterator();
            return new Iterator<O>() {
                @Override
                public boolean hasNext() {
                    return originIterator.hasNext();
                }

                @Override
                public O next() {
                    return map(originIterator.next());
                }
            };
        }

        private MapIterable(Iterable<I> origin, Function<? super I, ? extends O> mapper) {
            this.origin = origin;
            this.mapper = mapper;
        }

        private O map(I input) {
            return mapper.call(input);
        }
    }

    private static class FlatMapIterable<I, O> implements Iterable<O> {

        private final Iterable<I> origin;
        private final Function<? super I, ? extends Stream<? extends O>> mapper;

        private FlatMapIterable(Iterable<I> origin, Function<? super I, ? extends Stream<? extends O>> mapper) {
            this.origin = origin;
            this.mapper = mapper;
        }

        @Override
        public Iterator<O> iterator() {
            final Iterator<I> streamsIterator = origin.iterator();
            return new Iterator<O>() {

                private Iterator<? extends O> currentStreamIterator;

                @Override
                public boolean hasNext() {
                    if (streamsIterator.hasNext() && currentStreamIterator == null) {
                        currentStreamIterator = getCurrentStream().iterator();
                        return checkCurrentStreamHasItems();
                    } else {
                        return currentStreamIterator != null && checkCurrentStreamHasItems();
                    }
                }

                @Override
                public O next() {
                    return currentStreamIterator.next();
                }

                private Stream<? extends O> getCurrentStream() {
                    return mapper.call(streamsIterator.next());
                }

                private boolean checkCurrentStreamHasItems() {
                    if (currentStreamIterator.hasNext()) {
                        return true;
                    } else {
                        currentStreamIterator = null;
                        return hasNext();
                    }
                }
            };
        }

    }

}
