package de.sustineo.simdesk.services.weather;


import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Entity;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.AccWeatherSettings;
import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import lombok.Getter;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Profile(ProfileManager.PROFILE_MAP)
@Service
public class WeatherService {
    private static final String OPENWEATHERMAP_ONE_CALL_3_TEMPLATE = "https://api.openweathermap.org/data/3.0/onecall?lat=%s&lon=%s&exclude=minutely,daily,alerts&units=metric&appid=%s";

    private final RestTemplate restTemplate;

    private final HashMap<Track, OpenWeatherModel> weatherModelsByTrack = new HashMap<>();
    private final String openweathermapApiKey;
    @Getter
    private Instant lastUpdate;

    public WeatherService(RestTemplate restTemplate,
                          @Value("${simdesk.openweathermap.api-key}") String openweathermapApiKey) {
        this.restTemplate = restTemplate;
        this.openweathermapApiKey = openweathermapApiKey;
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

    public Optional<AccWeatherSettings> getAccWeatherSettings(Track track, int raceHours) {
        OpenWeatherModel weatherModel = weatherModelsByTrack.get(track);
        if (weatherModel == null) {
            return Optional.empty();
        }

        // Set minium race length to one hour
        if (raceHours <= 0) {
            raceHours = 1;
        }

        double temperatureSum = 0.0;
        double cloudsSum = 0.0;
        double probabilityOfPrecipitationSum = 0.0;

        for (int i = 0; i < raceHours; i++) {
            temperatureSum += weatherModel.getHourly().get(i).getTemperature();
            cloudsSum += weatherModel.getHourly().get(i).getClouds();
            probabilityOfPrecipitationSum += weatherModel.getHourly().get(i).getProbabilityOfPrecipitation();
        }

        double averageTemperature = temperatureSum / raceHours;
        double averageClouds = cloudsSum / raceHours;
        double averageProbabilityOfPrecipitation = probabilityOfPrecipitationSum / raceHours;

        AccWeatherSettings weatherSettings = AccWeatherSettings.builder()
                .weatherModel(weatherModel)
                .ambientTemperature((int) averageTemperature)
                .cloudLevel(averageClouds / 100)
                .rainLevel(averageProbabilityOfPrecipitation / 100)
                .randomness(0)
                .build();

        return Optional.of(weatherSettings);
    }

    /**
     * This method calculates the percentage range based on the rain intensity in mm/h.
     * The formula is defined piecewise to match the categories from the table.
     *
     * <pre>
     * | Rain Classification     | Intensität (mm/h)    | Prozentbereich (%)     |
     * |-------------------------|----------------------|------------------------|
     * | No rain                 | < 0.5 mm/h           | 0 %                    |
     * | Weak rain               | 0.5–2 mm/h           | 2–7 %                  |
     * | Moderate rain           | 2–6 mm/h             | 7–20 %                 |
     * | Heavy rain              | 6–10 mm/h            | 20–33 %                |
     * | Very heavy rain         | 10–18 mm/h           | 33–60 %                |
     * | Shower                  | 18–30 mm/h           | 60–100 %               |
     * | Cloudburst              | > 30 mm/h            | > 100 %                |
     * </pre>
     *
     * @param rainIntensity Rain intensity in mm/h
     * @return Percentage value corresponding to the given intensity
     */
    protected double convertRainIntensityToPercentage(double rainIntensity) {
        if (rainIntensity < 0.5) { // No rain (<0.5 mm/h)
            return 0.0;
        } else if (rainIntensity >= 0.5 && rainIntensity < 2) { // Weak rain (0.5–2 mm/h)
            return ((2.0 / 1.5) * (rainIntensity - 0.5) + 2) / 100;
        } else if (rainIntensity >= 2 && rainIntensity < 6) { // Moderate rain (2–6 mm/h)
            return ((13.0 / 4.0) * (rainIntensity - 2) + 7) / 100;
        } else if (rainIntensity >= 6 && rainIntensity < 10) { // Heavy rain (6–10 mm/h)
            return ((13.0 / 4.0) * (rainIntensity - 6) + 20) / 100;
        } else if (rainIntensity >= 10 && rainIntensity < 18) { // Very heavy rain (10–18 mm/h)
            return ((27.0 / 8.0) * (rainIntensity - 10) + 33) / 100;
        } else if (rainIntensity >= 18 && rainIntensity <= 30) { // Shower (18–30 mm/h)
            return ((40.0 / 12.0) * (rainIntensity - 18) + 60) / 100;
        } else { // Cloudburst (> 30 mm/h)
            return 1.0;
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 * * * *")
    private void updateCurrentWeather() {
        if (isApiKeyMissing()) {
            return;
        }

        // Fetch current weather data from OpenWeatherMap API
        for (Track track : Track.getAllSortedByNameForAcc()) {
            // Only fetch weather data for Kyalami Circuit in debug mode
            if (ProfileManager.isDebug() && !track.getAccId().equals("kyalami")) {
                continue;
            }

            try {
                String url = String.format(OPENWEATHERMAP_ONE_CALL_3_TEMPLATE, track.getLatitude(), track.getLongitude(), openweathermapApiKey);
                log.info(String.format("Fetching weather data for track '%s' from %s", track.getName(), StringUtils.replace(url, openweathermapApiKey, Entity.HIDDEN)));

                OpenWeatherModel weatherModel = restTemplate.getForObject(url, OpenWeatherModel.class);
                weatherModelsByTrack.put(track, weatherModel);
            } catch (HttpStatusCodeException e) {
                log.log(Level.SEVERE, "Failed to fetch weather data for track " + track.getName() + ": " + e.getMessage(), e);
            }
        }

        lastUpdate = Instant.now();
    }
}
