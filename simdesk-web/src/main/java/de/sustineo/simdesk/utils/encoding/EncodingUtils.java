package de.sustineo.simdesk.utils.encoding;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Utility class for handling different character encodings when reading file data.
 * Supports UTF-8 and UTF-16 (with BOM detection).
 */
@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncodingUtils {
    private static final Map<Charset, ThreadLocal<CharsetDecoder>> DECODERS = new ConcurrentHashMap<>();
    private static final Map<Charset, ThreadLocal<CharsetEncoder>> ENCODERS = new ConcurrentHashMap<>();
    private static final Pattern CONTROL_CHARS_PATTERN = Pattern.compile("[\\p{Cc}&&[^\\r\\n\\t]]");

    // Sampling and heuristic thresholds
    private static final int SAMPLE_SIZE = 8 * 1024; // 8KB sample for heuristics
    private static final int MIN_CONFIDENT_LENGTH = 16; // below this, heuristics are weak
    private static final double ZERO_RATIO_THRESHOLD = 0.30; // fraction of zeros to consider UTF-16-like
    private static final double ZERO_EVEN_ODD_RATIO = 2.0; // relative preference even vs odd zero counts

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
            // Use cached decoder for consistent behavior and avoid extra allocations
            CharsetDecoder decoder = getDecoder(charset);
            CharBuffer chars = decoder.decode(ByteBuffer.wrap(data));

            String content = chars.toString();
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

        // For tiny inputs heuristics are weak; fall back to UTF-8
        if (data.length < MIN_CONFIDENT_LENGTH) {
            return StandardCharsets.UTF_8;
        }

        // Sample the beginning of the data for heuristics
        byte[] sample = Arrays.copyOf(data, Math.min(data.length, SAMPLE_SIZE));

        UTF16Guess utf16Guess = guessUTF16ByZeroBytes(sample);

        // UTF-16 heuristic: presence of zero bytes suggests UTF-16 and overall data length must be even
        if ((data.length % 2) == 0 && utf16Guess != UTF16Guess.NONE) {
            Charset charsetCandidate = (utf16Guess == UTF16Guess.LE) ? StandardCharsets.UTF_16LE : StandardCharsets.UTF_16BE;
            if (roundTripMatches(sample, charsetCandidate)) {
                return charsetCandidate;
            }
        }

        // UTF-8 heuristic: presence of multibyte lead bytes and reasonable continuation bytes
        if (looksLikeUtf8(sample) && roundTripMatches(sample, StandardCharsets.UTF_8)) {
            return StandardCharsets.UTF_8;
        }

        // Default to UTF-8
        return StandardCharsets.UTF_8;
    }

    private static UTF16Guess guessUTF16ByZeroBytes(byte[] sample) {
        int zerosEven = 0;
        int zerosOdd = 0;
        int length = sample.length;

        for (int i = 0; i < length; i++) {
            if (sample[i] == 0) {
                if ((i % 2) == 0) {
                    zerosEven++;
                } else {
                    zerosOdd++;
                }
            }
        }

        double zeroRatio = (double) (zerosEven + zerosOdd) / length;
        if (zeroRatio < ZERO_RATIO_THRESHOLD) {
            return UTF16Guess.NONE;
        }

        if (zerosEven >= ZERO_EVEN_ODD_RATIO * Math.max(1, zerosOdd)) {
            return UTF16Guess.LE;
        }

        if (zerosOdd >= ZERO_EVEN_ODD_RATIO * Math.max(1, zerosEven)) {
            return UTF16Guess.BE;
        }

        return UTF16Guess.NONE;
    }

    private static boolean looksLikeUtf8(byte[] sample) {
        int leadCount = 0;
        int contCount = 0;

        for (byte value : sample) {
            int b = value & 0xFF;
            if (b >= 0xC2 && b <= 0xF4) {
                leadCount++;
            } else if (b >= 0x80 && b <= 0xBF) {
                contCount++;
            }
        }

        // require at least one lead and at least some continuation bytes
        return leadCount > 0 && contCount >= Math.max(1, leadCount / 2);
    }

    /**
     * Performs a strict round-trip check: decode bytes with the given charset using
     * error reporting, then re-encode and compare the resulting bytes to the original.
     *
     * @param data    the original bytes
     * @param charset the charset to test
     * @return true if decoding+encoding produces exactly the original bytes
     */
    private static boolean roundTripMatches(byte[] data, Charset charset) {
        try {
            CharsetDecoder decoder = getDecoder(charset);
            CharBuffer chars = decoder.decode(ByteBuffer.wrap(data));

            CharsetEncoder encoder = getEncoder(charset);
            ByteBuffer encoded = encoder.encode(chars);

            byte[] encodedBytes = new byte[encoded.remaining()];
            encoded.get(encodedBytes);

            return Arrays.equals(data, encodedBytes);
        } catch (CharacterCodingException e) {
            return false;
        }
    }

    public static CharsetDecoder getDecoder(Charset charset) {
        ThreadLocal<CharsetDecoder> threadLocal = DECODERS.computeIfAbsent(charset,
                c -> ThreadLocal.withInitial(() -> c.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)));
        CharsetDecoder decoder = threadLocal.get();
        decoder.reset();
        return decoder;
    }

    public static CharsetEncoder getEncoder(Charset charset) {
        ThreadLocal<CharsetEncoder> threadLocal = ENCODERS.computeIfAbsent(charset,
                c -> ThreadLocal.withInitial(() -> c.newEncoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)));
        CharsetEncoder encoder = threadLocal.get();
        encoder.reset();
        return encoder;
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
