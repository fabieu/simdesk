package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.output.ServiceResponse;
import de.sustineo.simdesk.entities.output.SessionResponse;
import de.sustineo.simdesk.services.leaderboard.LapService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
@PreAuthorize("hasAnyRole('ADMIN')")
public class DriverController {
    private final SessionService sessionService;
    private final LapService lapService;

    public DriverController(SessionService sessionService,
                            LapService lapService) {
        this.sessionService = sessionService;
        this.lapService = lapService;
    }

    @Operation(summary = "Get all sessions")
    @GetMapping(path = "/drivers/{driverId}/sessions")
    public ResponseEntity<ServiceResponse<List<SessionResponse>>> getSessions(@PathVariable String driverId,
                                                                              @RequestParam(name = "withLaps", required = false) boolean withLaps,
                                                                              @RequestParam(name = "from", required = false) Instant fromParam,
                                                                              @RequestParam(name = "to", required = false) Instant toParam) {
        Instant from = Objects.requireNonNullElse(fromParam, Instant.EPOCH);
        Instant to = Objects.requireNonNullElse(toParam, Instant.now());

        List<SessionResponse> sessionResponse = sessionService.getAllByTimeRangeAndDriverId(from, to, driverId).stream()
                .map(SessionResponse::new)
                .toList();

        if (withLaps) {
            sessionResponse.forEach(session -> session.setLaps(lapService.getBySessionIdAndDriverId(session.getId(), driverId)));
        }

        return new ServiceResponse<>(HttpStatus.OK, sessionResponse).toResponseEntity();
    }
}
