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

    private static final Function<String, String> UPPERCASE = new Function<String, String>() {
        @Override
        public String call(String s) {
            return s.toUpperCase();
        }
    };

    private static final Function<String, Stream<Character>> CHARACTERS = new Function<String, Stream<Character>>() {
        @Override
        public Stream<Character> call(String s) {
            List<Character> characters = new ArrayList<>();
            char[] chars = s.toCharArray();
            for (char aChar : chars) {
                characters.add(aChar);
            }
            return Streams.from(characters);
        }
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

        characterStream.forEach(new Consumer<Character>(){
            @Override
            public void accept(Character value) {
                logger.log(value);
            }
        });
        inOrder.verify(logger, calls(12)).log(anyChar());

        characterStream.forEach(new Consumer<Character>(){
            @Override
            public void accept(Character value) {
                logger.log(value);
            }
        });
        inOrder.verify(logger, calls(12)).log(anyChar());
    }
}
