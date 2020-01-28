package io.github.eleventigerssc.interview.morse;

import java.util.HashMap;
import java.util.Map;

/**
 * The Morse code encodes every character as a sequence of "dots" and "dashes". For example, the letter A is coded
 * as ·−, letter Q is coded as −−·−, and digit 1 is coded as ·−−−−. The Morse code is case-insensitive, traditionally
 * capital letters are used. When the message is written in Morse code, a single space is used to separate the character
 * codes and 3 spaces are used to separate words. For example, the message HEY JUDE in Morse code is
 * ···· · −·−− ·−−− ··− −·· ·.
 */
final class Morse {

    private static final Map<CharSequence, Character> MORSE_TO_CHAR = new HashMap<CharSequence, Character>() {
        {
            put(".-", 'A');
            put("-...", 'B');
            put("-.-.", 'C');
            put("-..", 'D');
            put(".", 'E');
            put("..-.", 'F');
            put("--.", 'G');
            put("....", 'H');
            put("..", 'I');
            put(".---", 'J');
            put("-.-", 'K');
            put(".-..", 'L');
            put("--", 'M');
            put("-.", 'N');
            put("---", 'O');
            put(".--.", 'P');
            put("--.-", 'Q');
            put(".-.", 'R');
            put("...", 'S');
            put("-", 'T');
            put("..-", 'U');
            put("...-", 'V');
            put(".--", 'W');
            put("-..-", 'X');
            put("-.--", 'Y');
            put("--..", 'Z');

            put("-----", '0');
            put(".----", '1');
            put("..---", '2');
            put("...--", '3');
            put("....-", '4');
            put(".....", '5');
            put("-....", '6');
            put("--...", '7');
            put("---..", '8');
            put("----.", '9');

            put("--··--", ',');
            put("·-·-·-", '.');
            put("··--··", '?');

            put("   ", ' ');

        }
    };

    private static final Map<Character, CharSequence> CHAR_TO_MORSE = new HashMap<Character, CharSequence>() {
        {
            for (Entry<CharSequence, Character> entry : MORSE_TO_CHAR.entrySet()) {
                put(entry.getValue(), entry.getKey());
            }
        }
    };

    public static Character decode(CharSequence charSequence) {
        Character character = MORSE_TO_CHAR.get(charSequence);
        if (character == null) {
            throw new IllegalArgumentException("Unexpected Morse code: " + charSequence);
        } else {
            return character;
        }
    }

    public static CharSequence encode(Character character) {
        CharSequence charSequence = CHAR_TO_MORSE.get(character);
        if (charSequence == null) {
            throw new IllegalArgumentException("Unexpected character: " + character);
        } else {
            return charSequence;
        }
    }

    private Morse() {
        throw new AssertionError("No instances");
    }
}
