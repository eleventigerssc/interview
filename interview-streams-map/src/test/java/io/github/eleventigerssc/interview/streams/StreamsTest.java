package io.github.eleventigerssc.interview.streams;

import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.Mockito.*;

public class StreamsTest {

    private static final String HELLO_WORLD[] = { "Hello", ",", "World", "!" };

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

    private final Logger logger = spy(new SystemLogger());
    private final List<String> strings = spy(new ArrayList<>(Arrays.asList(HELLO_WORLD)));

    @Test
    public void map_flatMap_filter_forEach() {
        // Create a stream from an iterable of Strings
        Stream<String> stream = Streams.from(strings);
        // strings object should not be touched at this point, think lazy
        verifyZeroInteractions(strings);

        // Create a mapped stream of Strings that are uppercase
        Stream<String> upperCaseStrings = stream.map(UPPERCASE);
        // Mapping should be a lazy operation, don't touch the string object yet
        verifyZeroInteractions(strings);

        // Decompose(flatMap) the uppercase strings stream into individual character stream
        Stream<Character> characterStream = upperCaseStrings.flatMap(CHARACTERS);
        // flatMap is lazy too
        verifyZeroInteractions(strings);

        // Filter the individual character stream into a stream of letters only
        Stream<Character> onlyLetters = characterStream.filter(ONLY_LETTERS);
        // filter is lazy too
        verifyZeroInteractions(strings);

        // Just a test util
        InOrder inOrder = inOrder(logger);

        // We finally use the result stream here, initial strings iterable may be touched
        onlyLetters.forEach(logger::log);
        inOrder.verify(logger, calls(10)).log(anyChar());

        // Let's try iterate again to see if forEach is stateless
        onlyLetters.forEach(logger::log);
        inOrder.verify(logger, calls(10)).log(anyChar());
    }
}
