package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.entities.output.ServiceResponse;
import de.sustineo.simdesk.entities.output.SessionResponse;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('ADMIN')")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
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
}
