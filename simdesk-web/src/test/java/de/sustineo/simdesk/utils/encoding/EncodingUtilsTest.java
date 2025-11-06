package de.sustineo.simdesk.utils.encoding;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EncodingUtilsTest {

    @Test
    void testBytesToString_WithUTF8() throws IOException {
        String originalText = "{\"test\": \"Hello World\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_8);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUTF8BOM() throws IOException {
        String originalText = "{\"test\": \"Hello World\"}";
        // UTF-8 BOM: EF BB BF
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = originalText.getBytes(StandardCharsets.UTF_8);
        byte[] data = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, data, 0, bom.length);
        System.arraycopy(content, 0, data, bom.length, content.length);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUTF16LE() throws IOException {
        String originalText = "{\"test\": \"Hello World\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_16LE);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUTF16LEBOM() throws IOException {
        String originalText = "{\"test\": \"Hello World\"}";
        // UTF-16LE BOM: FF FE
        byte[] bom = new byte[]{(byte) 0xFF, (byte) 0xFE};
        byte[] content = originalText.getBytes(StandardCharsets.UTF_16LE);
        byte[] data = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, data, 0, bom.length);
        System.arraycopy(content, 0, data, bom.length, content.length);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUTF16BE() throws IOException {
        String originalText = "{\"test\": \"Hello World\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_16BE);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUTF16BEBOM() throws IOException {
        String originalText = "{\"test\": \"Hello World\"}";
        // UTF-16BE BOM: FE FF
        byte[] bom = new byte[]{(byte) 0xFE, (byte) 0xFF};
        byte[] content = originalText.getBytes(StandardCharsets.UTF_16BE);
        byte[] data = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, data, 0, bom.length);
        System.arraycopy(content, 0, data, bom.length, content.length);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUTF16() throws IOException {
        String originalText = "{\"test\": \"Hello World\"}";
        // UTF-16 with default BOM (usually BE)
        byte[] data = originalText.getBytes(StandardCharsets.UTF_16);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithEmptyArray() throws IOException {
        byte[] data = new byte[0];

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEmpty();
    }

    @Test
    void testBytesToString_WithNull() throws IOException {
        String result = EncodingUtils.bytesToString(null);

        assertThat(result).isEmpty();
    }

    @Test
    void testBytesToString_WithControlCharacters() throws IOException {
        // JSON with control characters that should be removed
        String originalText = "{\"test\": \"Hello\u0000World\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_8);

        String result = EncodingUtils.bytesToString(data);

        // Control character (null byte) should be removed
        assertThat(result).isEqualTo("{\"test\": \"HelloWorld\"}");
    }

    @Test
    void testBytesToString_WithComplexJSON() throws IOException {
        String originalJson = """
                {
                  "entries": [
                    {
                      "trackId": "monza",
                      "carId": 1,
                      "ballastKg": 10,
                      "restrictor": 5
                    }
                  ]
                }
                """;
        byte[] data = originalJson.getBytes(StandardCharsets.UTF_8);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalJson);
    }

    @Test
    void testBytesToString_WithUnicodeCharacters() throws IOException {
        String originalText = "{\"test\": \"H\u00E9llo W\u00F6rld 你好\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_8);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUnicodeCharactersInUTF16() throws IOException {
        String originalText = "{\"test\": \"H\u00E9llo W\u00F6rld 你好\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_16LE);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithDeterministicBinary_ThrowsIOException() {
        // Construct deterministic bytes that are invalid as UTF-8 (many lone lead bytes)
        byte[] binary = new byte[1024];
        Arrays.fill(binary, (byte) 0xC0); // 0xC0 is not a valid UTF-8 lead sequence on its own

        assertThatThrownBy(() -> EncodingUtils.bytesToString(binary))
                .isInstanceOf(IOException.class);
    }

    @Test
    void testBytesToString_LargeUtf8Input_SampledDecoding() throws IOException {
        // Create a large UTF-8 string > sample size to exercise sampling logic
        StringBuilder sb = new StringBuilder();
        String chunk = "{\"k\": \"Some text with unicode H\u00E9\"}";
        for (int i = 0; i < 2000; i++) { // produce a few hundred KB
            sb.append(chunk).append('\n');
        }
        String large = sb.toString();
        byte[] data = large.getBytes(StandardCharsets.UTF_8);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(large);
    }

    @Test
    void testBytesToString_Utf16WithoutBOM_Detection_LE_and_BE() throws IOException {
        String ascii = "Hello world - detect utf16 without bom\n"; // ASCII chars produce lots of zero bytes in UTF-16 pairs
        String largeAscii = ascii.repeat(100);

        // UTF-16LE without BOM
        byte[] le = largeAscii.getBytes(StandardCharsets.UTF_16LE);
        String decodedLe = EncodingUtils.bytesToString(le);
        assertThat(decodedLe).isEqualTo(largeAscii);

        // UTF-16BE without BOM
        byte[] be = largeAscii.getBytes(StandardCharsets.UTF_16BE);
        String decodedBe = EncodingUtils.bytesToString(be);
        assertThat(decodedBe).isEqualTo(largeAscii);
    }

    @Test
    void testDecoderCache_ResetBetweenEncodings() throws IOException {
        String utf8 = "Simple ASCII text";
        byte[] u8 = utf8.getBytes(StandardCharsets.UTF_8);
        String out1 = EncodingUtils.bytesToString(u8);
        assertThat(out1).isEqualTo(utf8);

        String utf16 = "Another text with unicode H\u00E9";
        byte[] u16 = utf16.getBytes(StandardCharsets.UTF_16LE);
        String out2 = EncodingUtils.bytesToString(u16);
        assertThat(out2).isEqualTo(utf16);

        // decode utf8 again to ensure cached decoders/encoders were reset properly
        String out3 = EncodingUtils.bytesToString(u8);
        assertThat(out3).isEqualTo(utf8);
    }

    @Test
    void testShortInput_AmbiguousFallback() throws IOException {
        // Very short ASCII data (below the confident threshold) should still decode as UTF-8
        byte[] shortAscii = new byte[]{0x41, 0x42, 0x43}; // "ABC"
        String out = EncodingUtils.bytesToString(shortAscii);
        assertThat(out).isEqualTo("ABC");
    }
}
