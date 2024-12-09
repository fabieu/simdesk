package de.sustineo.simdesk.entities.json.kunos.acc;

import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccWeatherSettings {
    public static final int MIN_TEMPERATURE = 10;
    public static final int MAX_TEMPERATURE = 40;
    public static final int MIN_CLOUD_LEVEL = 0;
    public static final int MAX_CLOUD_LEVEL = 1;
    public static final int MIN_RAIN_LEVEL = 0;
    public static final int MAX_RAIN_LEVEL = 1;
    public static final int MIN_RANDOMNESS = 0;
    public static final int MAX_RANDOMNESS = 4;

    private OpenWeatherModel weatherModel;
    private Integer ambientTemperature;
    private Double cloudLevel;
    private Double rainLevel;
    private Integer randomness;
}
