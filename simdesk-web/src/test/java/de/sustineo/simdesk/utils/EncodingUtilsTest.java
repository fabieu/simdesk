package de.sustineo.simdesk.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

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
        String originalText = "{\"test\": \"Héllo Wörld 你好\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_8);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }

    @Test
    void testBytesToString_WithUnicodeCharactersInUTF16() throws IOException {
        String originalText = "{\"test\": \"Héllo Wörld 你好\"}";
        byte[] data = originalText.getBytes(StandardCharsets.UTF_16LE);

        String result = EncodingUtils.bytesToString(data);

        assertThat(result).isEqualTo(originalText);
    }
}
