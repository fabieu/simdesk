package de.sustineo.simdesk.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {
    private final RandomStringUtils randomStringUtils = RandomStringUtils.secureStrong();

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * @param length the length of the generated string
     * @return a random alphanumeric string
     */
    public String generateRandomString(int length) {
        return randomStringUtils.next(length, true, true);
    }
}
