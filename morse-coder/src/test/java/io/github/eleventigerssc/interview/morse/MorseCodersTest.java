package io.github.eleventigerssc.interview.morse;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

public class MorseCodersTest {

    private final MorseCoder subject = MorseCoders.get();

    @Test()
    public void decodeSingleLineText() {
        String input = ".-- .... ---   -.-. --- -- -- ..- -. .. -.-. .- - . ...   .. -.   -- --- .-. ... .   " +
                "-.-. --- -.. .   .. -.   - .... .. ...   -.. .- -.--   .- -. -..   .- --. . ··--··";

        subject.decode(toInputStream(input))
                .test()
                .assertNoErrors()
                .assertValues(
                        "WHO",
                        " ",
                        "COMMUNICATES",
                        " ",
                        "IN",
                        " ",
                        "MORSE",
                        " ",
                        "CODE",
                        " ",
                        "IN",
                        " ",
                        "THIS",
                        " ",
                        "DAY",
                        " ",
                        "AND",
                        " ",
                        "AGE?"
                );
    }

    @Test()
    public void decodeMultiLineText() {
        String input = "-. . ...- . .-.\n   .- --. .- .. -. --··--\n   ..\n   .- --\n   " +
                "-.. --- .. -. --.\n   ... ..- -.-. ....\n   .- -.\n   " +
                ".. -. - . .-. ...- .. . .-- ·-·-·-";

        subject.decode(toInputStream(input))
                .test()
                .assertNoErrors()
                .assertValues(
                        "NEVER",
                        " ",
                        "AGAIN,",
                        " ",
                        "I",
                        " ",
                        "AM",
                        " ",
                        "DOING",
                        " ",
                        "SUCH",
                        " ",
                        "AN",
                        " ",
                        "INTERVIEW."
                );
    }

    @Test()
    public void encodeSingleLineText() {
        String input = "TALK IS CHEAP. SHOW ME THE CODE.";

        subject.encode(toInputStream(input))
                .test()
                .assertNoErrors()
                .assertValues(
                        "- .- .-.. -.-",
                        "   ",
                        ".. ...",
                        "   ",
                        "-.-. .... . .- .--. ·-·-·-",
                        "   ",
                        "... .... --- .--",
                        "   ",
                        "-- .",
                        "   ",
                        "- .... .",
                        "   ",
                        "-.-. --- -.. . ·-·-·-"
                );
    }

    @Test()
    public void encodeMultiLineText() {
        String input = "MOST \nIMPROVED \nTHINGS \nCAN \nBE \nIMPROVED.";

        subject.encode(toInputStream(input))
                .test()
                .assertNoErrors()
                .assertValues(
                        "-- --- ... -",
                        "   ",
                        ".. -- .--. .-. --- ...- . -..",
                        "   ",
                        "- .... .. -. --. ...",
                        "   ",
                        "-.-. .- -.",
                        "   ",
                        "-... .",
                        "   ",
                        ".. -- .--. .-. --- ...- . -.. ·-·-·-"
                );
    }

    @Test
    public void decodeDoesNotConsumeStreamBeforeSubscribed() {
        InputStream inputStream = mock(InputStream.class);

        subject.decode(inputStream);

        verifyNoInteractions(inputStream);
    }

    @Test
    public void encodeDoesNotConsumeStreamBeforeSubscribed() {
        InputStream inputStream = mock(InputStream.class);

        subject.encode(inputStream);

        verifyNoInteractions(inputStream);
    }

    private static InputStream toInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }
}
