package io.github.eleventigerssc.interview.morse;

import java.util.HashMap;
import java.util.Map;

final class MorseCoders {

    /**
     * @return your own implementation of {@link MorseCoder}.
     */
    static MorseCoder get() {
        return MorseCoder.NOOP;
    }

    /**
     * @return map of characters to Morse encoded strings that can be used to implement {@link MorseCoder}.
     */
    static Map<Character, String> charToMorse() {
        return CHARS_T0_MORSE;
    }

    private static final Map<Character, String> CHARS_T0_MORSE = new HashMap<Character, String>() {{
        put('a', ".-");
        put('b', "-...");
        put('c', "-.-.");
        put('d', "-..");
        put('e', ".");
        put('f', "..-.");
        put('g', "--.");
        put('h', "....");
        put('i', "..");
        put('j', ".---");
        put('k', "-.-");
        put('l', ".-..");
        put('m', "--");
        put('n', "-.");
        put('o', "---");
        put('p', ".--.");
        put('q', "--.-");
        put('r', ".-.");
        put('s', "...");
        put('t', "-");
        put('u', "..-");
        put('v', "...-");
        put('w', ".--");
        put('x', "-..-");
        put('y', "-.--");
        put('z', "--..");
        put('1', ".----");
        put('2', "..---");
        put('3', "...--");
        put('4', "....-");
        put('5', ".....");
        put('6', "-....");
        put('7', "--...");
        put('8', "---..");
        put('9', "----.");
        put('0', "-----");
        put('?', "··--··");
        put(',', "--··--");
        put('.', "·-·-·-");
    }};
}
