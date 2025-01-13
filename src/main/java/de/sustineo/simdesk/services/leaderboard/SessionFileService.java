package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.utils.json.JsonUtils;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class SessionFileService {
    private final List<Charset> SUPPORTED_CHARSETS = List.of(StandardCharsets.UTF_16LE, StandardCharsets.UTF_8);

    private final SessionService sessionService;

    public SessionFileService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void handleSessionFile(@Nonnull Path path) {
        try {
            handleSessionFile(path, null);
        } catch (Exception e) {
            log.log(Level.SEVERE, String.format("Could not process session file %s", path), e);
        }
    }

    @PreAuthorize("hasAnyAuthority(T(de.sustineo.simdesk.entities.auth.UserRole).ADMIN, T(de.sustineo.simdesk.entities.auth.UserRole).MANAGER)")
    public void handleSessionFileWithSessionDatetimeOverride(@Nonnull Path path, @Nullable Instant sessionDatetimeOverride) throws Exception {
        handleSessionFile(path, sessionDatetimeOverride);
    }

    private void handleSessionFile(@Nonnull Path path, @Nullable Instant sessionDatetimeOverride) throws Exception {
        if (!Files.isRegularFile(path)) {
            String errorMessage = String.format("File %s is not a file", path.getFileName());
            log.warning(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String fileContent = readFile(path);
        if (!JsonUtils.isValid(fileContent)) {
            String errorMessage = String.format("File %s is not a valid JSON file", path.getFileName());
            log.warning(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        FileMetadata fileMetadata = new FileMetadata(path);

        if (sessionDatetimeOverride != null) {
            fileMetadata.setModifiedDatetime(sessionDatetimeOverride);
        }

        AccSession accSession = JsonUtils.fromJson(fileContent, AccSession.class);
        sessionService.handleSession(accSession, fileContent, fileMetadata);
    }

    private String readFile(Path path) throws IOException {
        for (Charset charset : SUPPORTED_CHARSETS) {
            try {
                String fileContent = Files.readString(path, charset);
                fileContent = removeBOM(fileContent);
                fileContent = removeControlCharacters(fileContent);
                log.fine(String.format("Successfully parsed %s with charset %s", path, charset));

                return fileContent;
            } catch (IOException e) {
                log.fine(String.format("Unable to parse %s with charset %s", path, charset));
            }
        }

        throw new IOException(String.format("Could not parse %s with any the supported charsets: %s", path, SUPPORTED_CHARSETS));
    }

    private String removeBOM(String content) {
        final String BOM_MARKER = "\uFEFF";

        if (content.startsWith(BOM_MARKER)) {
            return content.substring(1);
        } else {
            return content;
        }
    }

    private String removeControlCharacters(String content) {
        return content.replaceAll("[\\x00-\\x09\\x11\\x12\\x14-\\x1F\\x7F]", "");
    }
}
