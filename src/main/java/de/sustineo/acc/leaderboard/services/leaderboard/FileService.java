package de.sustineo.acc.leaderboard.services.leaderboard;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@Service
public class FileService {
    public static final Pattern SESSION_FILE_PATTERN = Pattern.compile("^.*_((FP\\d*)|(Q\\d*)|(R\\d*)).json$");

    public String removeBOM(String s) {
        final String BOM_MARKER = "\uFEFF";

        if (s.startsWith(BOM_MARKER)) {
            return s.substring(1);
        } else {
            return s;
        }
    }

    public static boolean isSessionFile(Path file) {
        Matcher sessionFileMatcher = SESSION_FILE_PATTERN.matcher(file.getFileName().toString());

        if (sessionFileMatcher.matches()) {
            return true;
        } else {
            log.warning(String.format("Ignoring file %s because it does not match the session file pattern", file));
            return false;
        }
    }

    public String removeControlCharacters(String fileContent) {
        return fileContent.replaceAll("[\\x00-\\x09\\x11\\x12\\x14-\\x1F\\x7F]", "");
    }
}
