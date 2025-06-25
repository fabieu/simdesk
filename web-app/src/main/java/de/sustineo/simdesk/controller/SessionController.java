package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.output.ServiceResponse;
import de.sustineo.simdesk.entities.output.SessionResponse;
import de.sustineo.simdesk.services.leaderboard.LapService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
@PreAuthorize("hasAnyRole('ADMIN')")
@Tag(name = "Session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final LapService lapService;

    @Operation(
            summary = "Get all sessions",
            description = "Retrieve all sessions within a time range or insert time range. Optional parameter to include lap data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sessions"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/sessions")
    public ResponseEntity<ServiceResponse<List<SessionResponse>>> getSessions(
            @Parameter(in = ParameterIn.QUERY, description = "Whether to include laps for each session", schema = @Schema(type = "boolean", defaultValue = "false"))
            @RequestParam(name = "withLaps", required = false)
            boolean withLaps,
            @Parameter(in = ParameterIn.QUERY, description = "Start of session time range (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam(name = "from", required = false)
            Instant fromParam,
            @Parameter(in = ParameterIn.QUERY, description = "End of session time range (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam(name = "to", required = false)
            Instant toParam,
            @Parameter(in = ParameterIn.QUERY, description = "Start of insert time range (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam(name = "insertFrom", required = false)
            Instant insertFromParam,
            @Parameter(in = ParameterIn.QUERY, description = "End of insert time range (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam(name = "insertTo", required = false)
            Instant insertToParam
    ) {
        // Prevent both from and to and insertFrom and insertTo being set
        if ((fromParam != null || toParam != null) && (insertFromParam != null || insertToParam != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Set default time range values
        Instant from = Instant.EPOCH;
        Instant to = Instant.now();

        List<Session> sessions;
        if (insertFromParam != null || insertToParam != null) {
            if (insertFromParam != null) {
                from = insertFromParam;
            }

            if (insertToParam != null) {
                to = insertToParam;
            }

            sessions = sessionService.getAllByInsertTimeRange(from, to);
        } else {
            if (toParam != null) {
                to = toParam;
            }

            if (fromParam != null) {
                from = fromParam;
            }

            sessions = sessionService.getAllBySessionTimeRange(from, to);
        }

        List<SessionResponse> sessionResponse = sessions.stream()
                .map(session -> createSessionResponse(session, withLaps))
                .toList();

        return new ServiceResponse<>(HttpStatus.OK, sessionResponse).toResponseEntity();
    }

    @Operation(
            summary = "Get session by file checksum",
            description = "Retrieve a session by its file checksum. Optionally include lap data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved session"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/sessions/{fileChecksum}")
    public ResponseEntity<ServiceResponse<SessionResponse>> getSession(
            @Parameter(in = ParameterIn.PATH, description = "The file checksum identifying the session", required = true)
            @PathVariable
            String fileChecksum,
            @Parameter(in = ParameterIn.QUERY, description = "Whether to include laps for the session", schema = @Schema(type = "boolean", defaultValue = "false"))
            @RequestParam(name = "withLaps", required = false)
            boolean withLaps
    ) {
        Session session = sessionService.getByFileChecksum(fileChecksum);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        SessionResponse sessionResponse = createSessionResponse(session, withLaps);

        return new ServiceResponse<>(HttpStatus.OK, sessionResponse).toResponseEntity();
    }

    private SessionResponse createSessionResponse(Session session, boolean withLaps) {
        SessionResponse sessionResponse = new SessionResponse(session);

        if (withLaps) {
            List<Lap> laps = lapService.getBySessionId(session.getId());
            sessionResponse.setLaps(laps);
        }

        return sessionResponse;
    }
}
