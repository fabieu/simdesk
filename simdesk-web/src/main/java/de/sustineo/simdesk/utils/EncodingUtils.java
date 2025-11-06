package de.sustineo.simdesk.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Utility class for handling different character encodings when reading file data.
 * Supports UTF-8 and UTF-16 (with BOM detection).
 */
@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncodingUtils {
    private static final Pattern CONTROL_CHARS_PATTERN = Pattern.compile("[\\p{Cc}&&[^\\r\\n\\t]]");

    /**
     * Converts a byte array to a string by trying multiple character encodings.
     * Attempts to detect and handle BOM (Byte Order Mark) for UTF-16 and UTF-8 files.
     *
     * @param data the byte array to convert
     * @return the string representation of the data
     * @throws IOException if the data cannot be decoded (e.g., malformed input for detected charset)
     */
    public static String bytesToString(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return "";
        }

        // Detect BOM and choose appropriate charset
        Charset charset = detectCharset(data);

        try {
            String content = new String(data, charset);
            content = removeBOM(content);
            content = removeControlCharacters(content);

            log.fine(String.format("Successfully parsed data with charset %s", charset));
            return content;
        } catch (Exception e) {
            // Handle malformed input or other decoding errors
            throw new IOException(String.format("Failed to decode data with charset %s: %s", charset, e.getMessage()), e);
        }
    }

    /**
     * Detects the character encoding of the data by checking for BOM markers.
     *
     * @param data the byte array to analyze
     * @return the detected charset, defaults to UTF-8 if no BOM is found
     */
    private static Charset detectCharset(byte[] data) {
        if (data == null || data.length < 2) {
            return StandardCharsets.UTF_8;
        }

        // Check for UTF-16 BOM (must check before UTF-8 as both start with similar patterns)
        // UTF-16LE BOM: FF FE
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xFE) {
            return StandardCharsets.UTF_16LE;
        }

        // UTF-16BE BOM: FE FF
        if (data[0] == (byte) 0xFE && data[1] == (byte) 0xFF) {
            return StandardCharsets.UTF_16BE;
        }

        // Check for UTF-8 BOM: EF BB BF
        if (data.length >= 3 && data[0] == (byte) 0xEF && data[1] == (byte) 0xBB && data[2] == (byte) 0xBF) {
            return StandardCharsets.UTF_8;
        }

        // No BOM detected, try to infer from content
        // Check if data looks like UTF-16 (many null bytes in even positions for ASCII-like text)
        if (data.length >= 4 && looksLikeUTF16LE(data)) {
            return StandardCharsets.UTF_16LE;
        }

        if (data.length >= 4 && looksLikeUTF16BE(data)) {
            return StandardCharsets.UTF_16BE;
        }

        // Default to UTF-8
        return StandardCharsets.UTF_8;
    }

    /**
     * Heuristic to detect if data looks like UTF-16LE encoded text.
     * UTF-16LE has null bytes in odd positions for ASCII characters.
     * <p>
     * Note: This heuristic may produce false positives for binary data with regular null byte patterns.
     * It is only used as a fallback when no BOM is present. The caller should validate the decoded result.
     */
    private static boolean looksLikeUTF16LE(byte[] data) {
        int nullInOddPositions = 0;
        int samplesChecked = 0;

        // Check first 100 bytes or less
        for (int i = 1; i < Math.min(100, data.length); i += 2) {
            if (data[i] == 0) {
                nullInOddPositions++;
            }
            samplesChecked++;
        }

        // If more than 70% of odd positions are null, likely UTF-16LE
        return samplesChecked > 0 && ((double) nullInOddPositions / samplesChecked) > 0.7;
    }

    /**
     * Heuristic to detect if data looks like UTF-16BE encoded text.
     * UTF-16BE has null bytes in even positions for ASCII characters.
     * <p>
     * Note: This heuristic may produce false positives for binary data with regular null byte patterns.
     * It is only used as a fallback when no BOM is present. The caller should validate the decoded result.
     */
    private static boolean looksLikeUTF16BE(byte[] data) {
        int nullInEvenPositions = 0;
        int samplesChecked = 0;

        // Check first 100 bytes or less
        for (int i = 0; i < Math.min(100, data.length); i += 2) {
            if (data[i] == 0) {
                nullInEvenPositions++;
            }
            samplesChecked++;
        }

        // If more than 70% of even positions are null, likely UTF-16BE
        return samplesChecked > 0 && ((double) nullInEvenPositions / samplesChecked) > 0.7;
    }

    /**
     * Removes the Byte Order Mark (BOM) from the beginning of a string if present.
     * Handles UTF-8, UTF-16LE, and UTF-16BE BOMs.
     *
     * @param content the string to process
     * @return the string without BOM
     */
    private static String removeBOM(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        // UTF-8 BOM: EF BB BF -> \uFEFF
        // UTF-16LE BOM: FF FE -> \uFEFF
        // UTF-16BE BOM: FE FF -> \uFEFF
        final String BOM_MARKER = "\uFEFF";

        if (content.startsWith(BOM_MARKER)) {
            return content.substring(1);
        }

        return content;
    }

    /**
     * Removes control characters from a string, except for common whitespace characters.
     * Keeps: \n (0x0A), \r (0x0D), \t (0x09)
     * Removes: everything in Unicode category Cc (includes ASCII control range and U+0080–U+009F, plus DEL)
     *
     * @param content the string to process
     * @return the string without control characters
     */
    private static String removeControlCharacters(String content) {
        if (content == null) {
            return null;
        }
        return CONTROL_CHARS_PATTERN.matcher(content).replaceAll("");
    }
}
