package io.github.eleventigerssc.interview.morse;

import java.io.InputStream;

import io.reactivex.Flowable;

public interface MorseCoder {

    Flowable<String> decode(InputStream inputStream);

    Flowable<String> encode(InputStream inputStream);

    MorseCoder NOOP = new MorseCoder() {

        @Override
        public Flowable<String> decode(InputStream inputStream) {
            return Flowable.empty();
        }

        @Override
        public Flowable<String> encode(InputStream inputStream) {
            return Flowable.empty();
        }
    };
}
