package de.sustineo.simdesk.services.weather;


import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Profile(ProfileManager.PROFILE_MAP)
@Service
public class WeatherService {
    private static final String OPENWEATHERMAP_ONE_CALL_3_TEMPLATE = "https://api.openweathermap.org/data/3.0/onecall?lat=%s&lon=%s&exclude=minutely,daily,alerts&units=metric&appid=%s";

    private final RestTemplate restTemplate;

    private final String openweathermapApiKey;
    private final HashMap<Track, OpenWeatherModel> weatherModelsByTrack = new HashMap<>();

    public WeatherService(RestTemplate restTemplate,
                          @Value("${simdesk.openweathermap.api-key}") String openweathermapApiKey) {
        this.restTemplate = restTemplate;
        this.openweathermapApiKey = openweathermapApiKey;
    }

    public Optional<OpenWeatherModel> getWeatherModel(Track track) {
        return Optional.ofNullable(weatherModelsByTrack.get(track));
    }

    private Optional<String> getMapUrlTemplate(String mapType) {
        if (isApiKeyMissing()) {
            return Optional.empty();
        }

        return Optional.of("https://tile.openweathermap.org/map/%s/{z}/{x}/{y}.png?appid=%s".formatted(mapType, openweathermapApiKey));
    }

    public Optional<String> getTemperatureMapUrlTemplate() {
        return getMapUrlTemplate("temp_new");
    }

    public Optional<String> getPrecipitationMapUrlTemplate() {
        return getMapUrlTemplate("precipitation_new");
    }

    public Optional<String> getCloudsMapUrlTemplate() {
        return getMapUrlTemplate("clouds_new");
    }

    private boolean isApiKeyMissing() {
        return openweathermapApiKey == null || openweathermapApiKey.isEmpty();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 * * * *")
    private void updateCurrentWeather() {
        if (isApiKeyMissing()) {
            return;
        }

        // Fetch current weather data from OpenWeatherMap API
        for (Track track : Track.getAllSortedByName()) {
            try {
                String url = String.format(OPENWEATHERMAP_ONE_CALL_3_TEMPLATE, track.getLatitude(), track.getLongitude(), openweathermapApiKey);
                log.info(String.format("Fetching weather data for track '%s' from %s", track.getName(), StringUtils.replace(url, openweathermapApiKey, "API_KEY")));

                OpenWeatherModel weatherModel = restTemplate.getForObject(url, OpenWeatherModel.class);
                weatherModelsByTrack.put(track, weatherModel);
            } catch (HttpStatusCodeException e) {
                log.log(Level.SEVERE, "Failed to fetch weather data for track " + track.getName() + ": " + e.getMessage(), e);
            }
        }
    }
}
