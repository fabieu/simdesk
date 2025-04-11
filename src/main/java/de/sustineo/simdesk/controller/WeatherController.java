package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.AccWeatherSettings;
import de.sustineo.simdesk.entities.output.WeatherSettingsOutput;
import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import de.sustineo.simdesk.services.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/current")
    public WeatherSettingsOutput getCurrentWeather(
            @RequestParam(value = "withWeatherModel", required = false, defaultValue = "false") boolean withWeatherModel,
            @RequestParam(value = "accTrackId", required = false) String accTrackId
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
        if (weatherModel.isPresent()) {
            AccWeatherSettings accWeatherSettings = weatherService.getCurrentAccWeatherSettings(weatherModel.get());
            weatherSettingsOutput.setAccSettings(accWeatherSettings);

            if (withWeatherModel) {
                weatherSettingsOutput.setWeatherModel(weatherModel.get());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No weather model found for track" + track);
        }

        return weatherSettingsOutput;
    }

    @GetMapping("/prediction")
    public WeatherSettingsOutput getPredictedWeather(
            @RequestParam(value = "raceHours") int raceHours,
            @RequestParam(value = "withWeatherModel", required = false, defaultValue = "false") boolean withWeatherModel,
            @RequestParam(value = "accTrackId", required = false) String accTrackId
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
        if (weatherModel.isPresent()) {
            AccWeatherSettings accWeatherSettings = weatherService.getPredictedAccWeatherSettings(weatherModel.get(), raceHours);
            weatherSettingsOutput.setAccSettings(accWeatherSettings);

            if (withWeatherModel) {
                weatherSettingsOutput.setWeatherModel(weatherModel.get());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No weather model found for track" + track);
        }

        return weatherSettingsOutput;
    }
}