package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.output.ServiceResponse;
import de.sustineo.simdesk.entities.output.SessionResponse;
import de.sustineo.simdesk.services.leaderboard.LapService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
@PreAuthorize("hasAnyRole('ADMIN')")
public class SessionController {
    private final SessionService sessionService;
    private final LapService lapService;

    public SessionController(SessionService sessionService,
                             LapService lapService) {
        this.sessionService = sessionService;
        this.lapService = lapService;
    }

    @Operation(summary = "Get all sessions")
    @GetMapping(path = "/sessions")
    public ResponseEntity<ServiceResponse<List<SessionResponse>>> getSessions(@RequestParam(name = "withLaps", required = false) boolean withLaps,
                                                                              @RequestParam(name = "from", required = false) Instant fromParam,
                                                                              @RequestParam(name = "to", required = false) Instant toParam,
                                                                              @RequestParam(name = "insertFrom", required = false) Instant insertFromParam,
                                                                              @RequestParam(name = "insertTo", required = false) Instant insertToParam) {
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

    @Operation(summary = "Get session by file checksum")
    @GetMapping(path = "/sessions/{fileChecksum}")
    public ResponseEntity<ServiceResponse<SessionResponse>> getSession(@PathVariable String fileChecksum,
                                                                       @RequestParam(name = "withLaps", required = false) boolean withLaps) {
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
