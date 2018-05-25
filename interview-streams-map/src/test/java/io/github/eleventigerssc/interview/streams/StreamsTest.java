package io.github.eleventigerssc.interview.streams;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class StreamsTest {

    private static final String TEST_STRINGS[] = { "Hello", ",", "World", "!" };
    private static final Character TEST_CHARACTERS[] = { 'H', 'e', 'l', 'l', 'o', 'W', 'o', 'r', 'l', 'd' };

    private static final Function<String, String> UPPERCASE = String::toUpperCase;

    private static final Function<String, Stream<Character>> CHARACTERS = s -> {
        List<Character> characters = new ArrayList<>();
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            characters.add(aChar);
        }
        return Streams.from(characters);
    };

    private static final Predicate<Character> ONLY_LETTERS = new Predicate<Character>() {

        private final Pattern pattern = Pattern.compile("[a-zA-Z]+\\.?");

        @Override
        public boolean test(Character character) {
            return pattern.matcher(character.toString()).matches();
        }
    };

    private final List<String> strings = spy(new ArrayList<>(Arrays.asList(TEST_STRINGS)));
    private final List<Character> characters = spy(new ArrayList<>(Arrays.asList(TEST_CHARACTERS)));

    @Test
    public void from_newStream_doesNotManipulateIterable() {
        Stream<String> stream = Streams.from(strings);

        assertNotNull(stream);
        verifyZeroInteractions(strings);
    }

    @Test
    public void forEach_iterateOverOriginal() {
        Stream<String> stream = Streams.from(strings);

        List<String> expected = Arrays.asList(TEST_STRINGS);
        List<String> actual = new ArrayList<>();
        stream.forEach(actual::add);

        assertEquals(expected, actual);
    }

    @Test
    public void map_usesSuppliedMapper() {
        Stream<String> upperCaseStrings = Streams.from(strings).map(UPPERCASE);
        verifyZeroInteractions(strings);

        List<String> expected = Arrays.asList("HELLO", ",", "WORLD", "!");
        List<String> actual = new ArrayList<>();
        upperCaseStrings.forEach(actual::add);

        assertEquals(expected, actual);
    }

    @Test
    public void flatMap_useSuppliedMapper() {
        Stream<Character> characterStream = Streams.from(strings).flatMap(CHARACTERS);
        verifyZeroInteractions(strings);

        List<Character> expected = Arrays.asList('H', 'e', 'l', 'l', 'o', ',', 'W', 'o', 'r', 'l', 'd', '!');
        List<Character> actual = new ArrayList<>();
        characterStream.forEach(actual::add);

        assertEquals(expected, actual);
    }

    @Test
    public void filter_useSuppliedPredicate() {
        Stream<Character> characterStream = Streams.from(characters).filter(ONLY_LETTERS);
        verifyZeroInteractions(strings);

        List<Character> expected = Arrays.asList(TEST_CHARACTERS);
        List<Character> actual = new ArrayList<>();
        characterStream.forEach(actual::add);

        assertEquals(expected, actual);
    }
}
