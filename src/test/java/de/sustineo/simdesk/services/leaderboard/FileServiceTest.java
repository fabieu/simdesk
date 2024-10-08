package de.sustineo.simdesk.services.leaderboard;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = FileService.class)
public class FileServiceTest {
    @Test
    public void isSessionFile() {
        assertThat(validateSessionFile("221012_220505_FP.json")).isTrue();
        assertThat(validateSessionFile("221012_220505_FP2.json")).isTrue();
        assertThat(validateSessionFile("221012_220505_Q.json")).isTrue();
        assertThat(validateSessionFile("221012_220505_Q3.json")).isTrue();
        assertThat(validateSessionFile("221012_220505_R.json")).isTrue();
        assertThat(validateSessionFile("221012_220505_R4.json")).isTrue();
        assertThat(validateSessionFile("")).isFalse();
        assertThat(validateSessionFile("test.json")).isFalse();
        assertThat(validateSessionFile("221012_220505_FP.txt")).isFalse();
    }

    private boolean validateSessionFile(String fileName) {
        return FileService.isSessionFile(Path.of(fileName));
    }
}
