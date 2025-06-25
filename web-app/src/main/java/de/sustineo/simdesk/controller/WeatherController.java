package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.AccWeatherSettings;
import de.sustineo.simdesk.entities.output.ServiceResponse;
import de.sustineo.simdesk.entities.output.WeatherSettingsOutput;
import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import de.sustineo.simdesk.services.weather.WeatherService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Controller for handling weather-related API endpoints.
 *
 * <p>This controller is only active when the application is running
 * under the {@link ProfileManager#PROFILE_MAP} profile.</p>
 *
 * <p>All endpoints in this controller require the user to have the 'ADMIN' role.</p>
 */
@Profile(ProfileManager.PROFILE_MAP)
@RestController
@RequestMapping(value = "/api/v1/weather", produces = "application/json")
@PreAuthorize("hasAnyRole('ADMIN')")
@Tag(name = "Weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @Operation(
            summary = "Get current weather settings",
            description = "Returns current weather prediction. Internally calls the prediction endpoint with raceHours = -1."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of weather settings"),
            @ApiResponse(responseCode = "404", description = "Track not found"),
            @ApiResponse(responseCode = "500", description = "Weather model unavailable")
    })
    @GetMapping("/current")
    public ResponseEntity<ServiceResponse<WeatherSettingsOutput>> getCurrentWeather(
            @Parameter(in = ParameterIn.QUERY, description = "Include weather model details in response", schema = @Schema(type = "boolean", defaultValue = "false"))
            @RequestParam(value = "withWeatherModel", required = false, defaultValue = "false")
            boolean withWeatherModel,
            @Parameter(in = ParameterIn.QUERY, description = "ACC Track ID")
            @RequestParam(value = "accTrackId", required = false)
            String accTrackId
    ) {
        return getPredictedWeather(-1, withWeatherModel, accTrackId);
    }

    @Operation(
            summary = "Get predicted weather settings",
            description = "Returns weather settings predicted for a given number of hours into the race."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of weather prediction"),
            @ApiResponse(responseCode = "404", description = "Track not found"),
            @ApiResponse(responseCode = "500", description = "Weather model unavailable")
    })
    @GetMapping("/prediction")
    public ResponseEntity<ServiceResponse<WeatherSettingsOutput>> getPredictedWeather(
            @Parameter(in = ParameterIn.QUERY, description = "Number of hours into the race", required = true)
            @RequestParam(value = "raceHours")
            int raceHours,
            @Parameter(in = ParameterIn.QUERY, description = "Include weather model details in response", schema = @Schema(type = "boolean", defaultValue = "false"))
            @RequestParam(value = "withWeatherModel", required = false)
            boolean withWeatherModel,
            @Parameter(in = ParameterIn.QUERY, description = "ACC Track ID")
            @RequestParam(value = "accTrackId", required = false)
            String accTrackId
    ) {
        WeatherSettingsOutput weatherSettingsOutput = new WeatherSettingsOutput();

        Track track = null;
        if (accTrackId != null) {
            track = Track.getByAccId(accTrackId);
        }

        if (track == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found: " + track);
        }

        Optional<OpenWeatherModel> weatherModel = weatherService.getOpenWeatherModel(track);
        if (weatherModel.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No weather model found for track" + track);
        }

        AccWeatherSettings accWeatherSettings = weatherService.getAccWeatherSettings(weatherModel.get(), raceHours);
        weatherSettingsOutput.setAccSettings(accWeatherSettings);

        if (withWeatherModel) {
            weatherSettingsOutput.setWeatherModel(weatherModel.get());
        }

        return new ServiceResponse<>(HttpStatus.OK, weatherSettingsOutput).toResponseEntity();
    }
}