package io.github.eleventigerssc.interview.streams;

import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private final Logger logger = spy(new SystemLogger());
    private final List<String> strings = spy(new ArrayList<>(Arrays.asList(HELLO_WORLD)));

    @Test
    public void map_flatMap_forEach() {
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

        // Just a test util
        InOrder inOrder = inOrder(logger);

        // We finally use the result stream here, initial strings iterable may be touched
        characterStream.forEach(logger::log);
        inOrder.verify(logger, calls(12)).log(anyChar());

        // Let's try iterate again to see if forEach is stateless
        characterStream.forEach(logger::log);
        inOrder.verify(logger, calls(12)).log(anyChar());
    }
}
