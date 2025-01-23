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
import java.util.Objects;

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
    public ResponseEntity<ServiceResponse<List<SessionResponse>>> getSessions(@RequestParam(name = "from", required = false) Instant fromParam,
                                                                              @RequestParam(name = "to", required = false) Instant toParam) {
        Instant from = Objects.requireNonNullElse(fromParam, Instant.EPOCH);
        Instant to = Objects.requireNonNullElse(toParam, Instant.now());

        List<SessionResponse> sessionResponse = sessionService.getAllByTimeRange(from, to).stream()
                .map(SessionResponse::new)
                .toList();

        return new ServiceResponse<>(HttpStatus.OK, sessionResponse).toResponseEntity();
    }

    @Operation(summary = "Get session by id")
    @GetMapping(path = "/sessions/{id}")
    public ResponseEntity<ServiceResponse<SessionResponse>> getSession(@PathVariable Integer id,
                                                                       @RequestParam(name = "withLaps", required = false) boolean withLaps) {
        Session session = sessionService.getById(id);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        SessionResponse sessionResponse = new SessionResponse(session);
        if (withLaps) {
            List<Lap> laps = lapService.getBySessionId(id);
            sessionResponse.setLaps(laps);
        }

        return new ServiceResponse<>(HttpStatus.OK, sessionResponse).toResponseEntity();
    }
}
