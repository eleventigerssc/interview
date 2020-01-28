package io.github.eleventigerssc.interview.morse;

import java.io.InputStream;

import io.reactivex.Flowable;

/**
 * Allows to decode Morse encoded stream to plain text word emissions and to encode plain text strings into Morse
 * encoded sequences.
 *
 * The Morse code encodes every character as a sequence of "dots" and "dashes". For example, the letter A is coded
 * as {@code .-}, letter Q is coded as {@code --.-}, and digit 1 is coded as {@code .−−−−}.
 * The Morse code is case-insensitive, traditionally capital letters are used. When the message is written in Morse code,
 * a single space is used to separate the character codes and 3 spaces are used to separate words.
 * For example, the message HEY JUDE in Morse code is {@code .... . -.--   .--- ..- -.. ..}.
 */
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
