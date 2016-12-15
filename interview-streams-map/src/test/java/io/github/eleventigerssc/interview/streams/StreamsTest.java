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
        Stream<String> stream = Streams.from(strings);
        verifyZeroInteractions(strings);

        Stream<String> upperCaseStrings = stream.map(UPPERCASE);
        verifyZeroInteractions(strings);

        Stream<Character> characterStream = upperCaseStrings.flatMap(CHARACTERS);
        verifyZeroInteractions(strings);

        InOrder inOrder = inOrder(logger);

        characterStream.forEach(logger::log);
        inOrder.verify(logger, calls(12)).log(anyChar());

        characterStream.forEach(logger::log);
        inOrder.verify(logger, calls(12)).log(anyChar());
    }
}
