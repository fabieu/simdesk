package de.sustineo.simdesk.services.weather;


import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Constants;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.AccWeatherSettings;
import de.sustineo.simdesk.entities.weather.OpenWeatherHourlyForecast;
import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import de.sustineo.simdesk.entities.weather.OpenWeatherPrecipitation;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Profile(SpringProfile.MAP)
@Service
public class WeatherService {
    private static final String OPENWEATHERMAP_ONE_CALL_3_TEMPLATE = "https://api.openweathermap.org/data/3.0/onecall?lat=%s&lon=%s&exclude=minutely,daily,alerts&units=metric&appid=%s";
    private static final int OPENWEATHERMAP_FORECAST_HOURS = 48;
    private static final int DEFAULT_RACE_HOURS = 1;

    private final RestClient restClient;

    private final HashMap<Track, OpenWeatherModel> weatherModelsByTrack = new HashMap<>();
    private final String openweathermapApiKey;
    @Getter
    private Instant lastUpdate;

    public WeatherService(RestClient restClient,
                          @Value("${simdesk.openweathermap.api-key}") String openweathermapApiKey) {
        this.restClient = restClient;
        this.openweathermapApiKey = openweathermapApiKey;
    }

    private boolean isApiKeyMissing() {
        return openweathermapApiKey == null || openweathermapApiKey.isEmpty();
    }

    private Optional<String> getOpenWeatherMapUrlTemplate(String mapType) {
        if (isApiKeyMissing()) {
            return Optional.empty();
        }

        return Optional.of("https://tile.openweathermap.org/map/%s/{z}/{x}/{y}.png?appid=%s".formatted(mapType, openweathermapApiKey));
    }

    public Optional<String> getOpenWeatherTemperatureMapUrlTemplate() {
        return getOpenWeatherMapUrlTemplate("temp_new");
    }

    public Optional<String> getOpenWeatherPrecipitationMapUrlTemplate() {
        return getOpenWeatherMapUrlTemplate("precipitation_new");
    }

    public Optional<String> getOpenWeatherCloudsMapUrlTemplate() {
        return getOpenWeatherMapUrlTemplate("clouds_new");
    }

    public Optional<OpenWeatherModel> getOpenWeatherModel(Track track) {
        return Optional.ofNullable(weatherModelsByTrack.get(track));
    }

    public AccWeatherSettings getAccWeatherSettings(OpenWeatherModel weatherModel, int raceHours) {
        // Set minimum and maximum boundaries for the weather model
        if (raceHours <= 0) {
            raceHours = DEFAULT_RACE_HOURS;
        } else if (raceHours > OPENWEATHERMAP_FORECAST_HOURS) {
            raceHours = OPENWEATHERMAP_FORECAST_HOURS;
        }

        double averageTemperature = calculateAverageTemperature(weatherModel, raceHours);
        double rainLevel = calculateRainLevel(weatherModel, raceHours);
        double cloudLevel = calculateCloudLevel(weatherModel, raceHours, rainLevel);
        int randomness = calculateRandomness(weatherModel, raceHours, rainLevel);

        return AccWeatherSettings.builder()
                .ambientTemperature(enforceAccTemperatureBoundaries(averageTemperature))
                .cloudLevel(enforceAccCloudLevelBoundaries(cloudLevel))
                .rainLevel(enforceAccRainLevelBoundaries(rainLevel))
                .randomness(enforceAccRandomnessBoundaries(randomness))
                .build();
    }

    /**
     * Calculates the average temperature over the specified number of race hours.
     *
     * @param weatherModel The weather model containing hourly weather data.
     * @param raceHours    The number of race hours to calculate the average temperature for.
     * @return The average temperature over the specified number of race hours.
     */
    protected double calculateAverageTemperature(OpenWeatherModel weatherModel, int raceHours) {
        int forecastHours = weatherModel.getHourly().size();
        double temperatureSum = 0.0;

        for (int i = 0; i < raceHours; i++) {
            if (i > forecastHours) {
                break;
            }

            temperatureSum += weatherModel.getHourly().get(i).getTemperature();
        }

        return temperatureSum / raceHours;
    }

    /**
     * Calculates the cloud level based on the maximum cloud coverage over the specified number of race hours.
     *
     * @param weatherModel The weather model containing hourly weather data.
     * @param raceHours    The number of race hours to calculate the cloud level for.
     * @return The cloud level over the specified number of race hours.
     */
    protected double calculateCloudLevel(OpenWeatherModel weatherModel, int raceHours, double rainLevel) {
        int forecastHours = weatherModel.getHourly().size();
        double cloudLevelSum = 0.0;
        int rainCount = 0;

        for (int i = 0; i < raceHours; i++) {
            if (i > forecastHours) {
                break;
            }

            cloudLevelSum += weatherModel.getHourly().get(i).getClouds();

            boolean isRaining = Optional.ofNullable(weatherModel.getHourly().get(i))
                    .map(OpenWeatherHourlyForecast::getRain)
                    .map(OpenWeatherPrecipitation::getPrecipitation)
                    .map(precipitation -> precipitation > 0.0)
                    .orElse(false);

            if (isRaining) {
                rainCount++;
            }
        }

        double averageCloudLevel = (cloudLevelSum / raceHours) / 100.0;
        if (rainLevel == 0.0) {
            return averageCloudLevel;
        }

        double rainDurationPercentage = (double) rainCount / raceHours;
        return rainDurationPercentage - rainLevel;
    }

    /**
     * Calculates the rain level based on the maximum rain intensity over the specified number of race hours.
     *
     * @param weatherModel The weather model containing hourly weather data.
     * @param raceHours    The number of race hours to calculate the rain level for.
     * @return The rain level over the specified number of race hours.
     */
    protected double calculateRainLevel(OpenWeatherModel weatherModel, int raceHours) {
        int forecastHours = weatherModel.getHourly().size();
        double rainIntensityMax = 0.0;

        for (int i = 0; i < raceHours; i++) {
            if (i > forecastHours) {
                break;
            }

            double rainIntensity = Optional.ofNullable(weatherModel.getHourly().get(i))
                    .map(OpenWeatherHourlyForecast::getRain)
                    .map(OpenWeatherPrecipitation::getPrecipitation)
                    .orElse(0.0);
            rainIntensityMax = Math.max(rainIntensityMax, rainIntensity);
        }

        return convertRainIntensityToPercentage(rainIntensityMax);
    }

    /**
     * Calculates the randomness based on the variance of the rain and cloud levels over the specified number of race hours.
     *
     * @param weatherModel The weather model containing hourly weather data.
     * @param raceHours    The number of race hours to calculate the randomness for.
     * @return The randomness over the specified number of race hours.
     */
    protected int calculateRandomness(OpenWeatherModel weatherModel, int raceHours, double rainLevel) {
        int forecastHours = weatherModel.getHourly().size();

        List<Double> rainProbabilities = new ArrayList<>();
        List<Double> cloudLevels = new ArrayList<>();

        for (int i = 0; i < raceHours; i++) {
            if (i > forecastHours) {
                break;
            }

            double rainProbability = weatherModel.getHourly().get(i).getProbabilityOfPrecipitation();
            rainProbabilities.add(rainProbability);

            double cloudLevel = weatherModel.getHourly().get(i).getClouds() / 100.0;
            cloudLevels.add(cloudLevel);
        }

        double rainModifier;
        double cloudModifier;
        if (rainLevel == 0.0) {
            rainModifier = 0.0;
            cloudModifier = 1.0; // Only consider cloud level if there is no rain
        } else {
            rainModifier = 0.7;
            cloudModifier = 0.3;
        }

        double rainVariance = calculateVariance(rainProbabilities);
        double cloudVariance = calculateVariance(cloudLevels);
        double combinedVariance = (rainModifier * rainVariance) + (cloudModifier * cloudVariance);

        // Scale the variance to range of [AccWeatherSettings.MIN_RANDOMNESS, AccWeatherSettings.MAX_RANDOMNESS] and round to the nearest integer
        long randomness = Math.round(combinedVariance * 4 * AccWeatherSettings.MAX_RANDOMNESS);

        return (int) randomness;
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
     * @return Percentage value corresponding to the given intensity, rounded to two decimal values
     */
    protected double convertRainIntensityToPercentage(double rainIntensity) {
        BigDecimal percentage;
        if (rainIntensity < 0.5) { // No rain (<0.5 mm/h)
            percentage = BigDecimal.ZERO;
        } else if (rainIntensity >= 0.5 && rainIntensity < 2) { // Weak rain (0.5–2 mm/h)
            percentage = BigDecimal.valueOf((2.0 / 1.5) * (rainIntensity - 0.5) + 2);
        } else if (rainIntensity >= 2 && rainIntensity < 6) { // Moderate rain (2–6 mm/h)
            percentage = BigDecimal.valueOf((13.0 / 4.0) * (rainIntensity - 2) + 7);
        } else if (rainIntensity >= 6 && rainIntensity < 10) { // Heavy rain (6–10 mm/h)
            percentage = BigDecimal.valueOf((13.0 / 4.0) * (rainIntensity - 6) + 20);
        } else if (rainIntensity >= 10 && rainIntensity < 18) { // Very heavy rain (10–18 mm/h)
            percentage = BigDecimal.valueOf((27.0 / 8.0) * (rainIntensity - 10) + 33);
        } else if (rainIntensity >= 18 && rainIntensity <= 30) { // Shower (18–30 mm/h)
            percentage = BigDecimal.valueOf((40.0 / 12.0) * (rainIntensity - 18) + 60);
        } else { // Cloudburst (> 30 mm/h)
            percentage = BigDecimal.valueOf(100);
        }

        return percentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * This method calculates the variance of a list of values.
     * The variance of a list of numbers measures how far the values are spread out from their mean.
     * By definition, variance is not inherently constrained to a particular range,
     * but for values between 0 and 1, the variance will naturally fall between 0 and 0.25.
     *
     * @param values List of values to calculate the variance for
     * @return The variance of the values
     */
    protected double calculateVariance(List<Double> values) {
        // Calculate the mean of the values
        double mean = values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // Calculate the variance of the values
        return values.stream()
                .mapToDouble(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);
    }

    /**
     * Enforces the boundaries for the ambient temperature.
     * The temperature is constrained to the range [10, 40] degrees Celsius.
     *
     * @param temperature The temperature to enforce the boundaries for
     * @return The temperature within the boundaries
     */
    private int enforceAccTemperatureBoundaries(double temperature) {
        return Math.min(Math.max((int) temperature, AccWeatherSettings.MIN_TEMPERATURE), AccWeatherSettings.MAX_TEMPERATURE);
    }

    /**
     * Enforces the boundaries for the cloud level.
     * The cloud level is constrained to the range [0, 1].
     *
     * @param cloudLevel The cloud level to enforce the boundaries for
     * @return The cloud level within the boundaries
     */
    private double enforceAccCloudLevelBoundaries(double cloudLevel) {
        double bounded = Math.min(Math.max(cloudLevel, AccWeatherSettings.MIN_CLOUD_LEVEL), AccWeatherSettings.MAX_CLOUD_LEVEL);
        return Math.round(bounded * 10_000.0) / 10_000.0; // Round to 4 decimal places
    }

    /**
     * Enforces the boundaries for the rain level.
     * The rain level is constrained to the range [0, 1].
     *
     * @param rainLevel The rain level to enforce the boundaries for
     * @return The rain level within the boundaries
     */
    private double enforceAccRainLevelBoundaries(double rainLevel) {
        double bounded = Math.min(Math.max(rainLevel, AccWeatherSettings.MIN_RAIN_LEVEL), AccWeatherSettings.MAX_RAIN_LEVEL);
        return Math.round(bounded * 10_000.0) / 10_000.0; // Round to 4 decimal places
    }


    /**
     * Enforces the boundaries for the randomness.
     * The randomness is constrained to the range [0, 4].
     *
     * @param randomness The randomness to enforce the boundaries for
     * @return The randomness within the boundaries
     */
    private int enforceAccRandomnessBoundaries(int randomness) {
        return Math.min(Math.max(randomness, AccWeatherSettings.MIN_RANDOMNESS), AccWeatherSettings.MAX_RANDOMNESS);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 * * * *")
    private void updateCurrentWeather() {
        if (isApiKeyMissing()) {
            return;
        }

        // Fetch current weather data from OpenWeatherMap API
        for (Track track : Track.getAll()) {
            // Only fetch weather data for Kyalami Circuit in debug mode
            if (SpringProfile.isDebug() && !track.getAccId().equals("kyalami")) {
                continue;
            }

            try {
                String url = String.format(OPENWEATHERMAP_ONE_CALL_3_TEMPLATE, track.getLatitude(), track.getLongitude(), openweathermapApiKey);
                log.info(String.format("Fetching weather data for track '%s' from %s", track.getName(), Strings.CS.replace(url, openweathermapApiKey, Constants.HIDDEN)));

                OpenWeatherModel weatherModel = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(OpenWeatherModel.class);
                weatherModelsByTrack.put(track, weatherModel);
            } catch (HttpStatusCodeException e) {
                log.log(Level.SEVERE, "Failed to fetch weather data for track " + track.getName() + ": " + e.getMessage(), e);
            }
        }

        lastUpdate = Instant.now();
    }
}
