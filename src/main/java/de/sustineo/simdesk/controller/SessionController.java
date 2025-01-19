package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.entities.output.SessionOutput;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('ADMIN')")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Get all sessions")
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionOutput>> getSessions() {
        List<SessionOutput> sessionOutputs = sessionService.getAll().stream()
                .map(SessionOutput::new)
                .toList();

        return ResponseEntity.of(Optional.of(sessionOutputs));
    }
}
