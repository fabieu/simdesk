package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.configuration.SpringProfile;
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

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Profile(SpringProfile.LEADERBOARD)
@RestController
@RequestMapping(value = "/api/v1/drivers", produces = "application/json")
@PreAuthorize("hasAnyRole('ADMIN')")
@Tag(name = "Driver")
@RequiredArgsConstructor
public class DriverController {
    private final SessionService sessionService;
    private final LapService lapService;

    @Operation(
            summary = "Get all sessions",
            description = "Retrieve all sessions for a given driver within an optional time range and optionally including lap data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sessions"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{driverId}/sessions")
    public ResponseEntity<ServiceResponse<List<SessionResponse>>> getSessions(
            @Parameter(in = ParameterIn.PATH, description = "The unique ID of the driver", required = true)
            @PathVariable
            String driverId,
            @Parameter(in = ParameterIn.QUERY, description = "Whether to include laps for each session", schema = @Schema(type = "boolean", defaultValue = "false"))
            @RequestParam(name = "withLaps", required = false)
            boolean withLaps,
            @Parameter(in = ParameterIn.QUERY, description = "Start of the time range (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam(name = "from", required = false)
            Instant fromParam,
            @Parameter(in = ParameterIn.QUERY, description = "End of the time range (ISO 8601 format)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam(name = "to", required = false)
            Instant toParam
    ) {
        Instant from = Objects.requireNonNullElse(fromParam, Instant.EPOCH);
        Instant to = Objects.requireNonNullElse(toParam, Instant.now());

        List<SessionResponse> sessionResponse = sessionService.getAllByTimeRangeAndDriverId(from, to, driverId).stream()
                .map(SessionResponse::new)
                .toList();

        if (withLaps) {
            sessionResponse.forEach(session -> session.setLaps(lapService.getBySessionIdAndDriverIds(session.getId(), List.of(driverId))));
        }

        return new ServiceResponse<>(HttpStatus.OK, sessionResponse).toResponseEntity();
    }
}
