package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.utils.encoding.EncodingUtils;
import de.sustineo.simdesk.utils.json.JsonClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.logging.Level;

@Log
@Profile(SpringProfile.LEADERBOARD)
@Service
@RequiredArgsConstructor
public class SessionFileService {
    private final SessionService sessionService;

    public void handleSessionFile(@Nonnull Path path) {
        try {
            handleSessionFile(path, null);
        } catch (Exception e) {
            log.log(Level.SEVERE, String.format("Could not process session file %s", path), e);
        }
    }

    @PreAuthorize("hasAnyAuthority(T(de.sustineo.simdesk.entities.auth.UserRoleEnum).ROLE_ADMIN, T(de.sustineo.simdesk.entities.auth.UserRoleEnum).ROLE_MANAGER)")
    public void handleSessionFileWithSessionDatetimeOverride(@Nonnull Path path, @Nullable Instant sessionDatetimeOverride) throws Exception {
        handleSessionFile(path, sessionDatetimeOverride);
    }

    private void handleSessionFile(@Nonnull Path path, @Nullable Instant sessionDatetimeOverride) throws Exception {
        if (!Files.isRegularFile(path)) {
            String errorMessage = String.format("File %s is not a file", path.getFileName());
            log.warning(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String fileContent = EncodingUtils.bytesToString(Files.readAllBytes(path));
        if (!JsonClient.isValid(fileContent)) {
            String errorMessage = String.format("File %s is not a valid JSON file", path.getFileName());
            log.warning(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        FileMetadata fileMetadata = new FileMetadata(path);

        if (sessionDatetimeOverride != null) {
            fileMetadata.setModifiedDatetime(sessionDatetimeOverride);
        }

        AccSession accSession = JsonClient.fromJson(fileContent, AccSession.class);

        sessionService.handleSession(accSession, fileContent, fileMetadata);
    }
}
