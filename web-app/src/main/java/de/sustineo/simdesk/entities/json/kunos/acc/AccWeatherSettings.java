package de.sustineo.simdesk.entities.json.kunos.acc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccWeatherSettings {
    public static final int MIN_TEMPERATURE = 10;
    public static final int MAX_TEMPERATURE = 35;
    public static final int MIN_CLOUD_LEVEL = 0;
    public static final int MAX_CLOUD_LEVEL = 1;
    public static final int MIN_RAIN_LEVEL = 0;
    public static final int MAX_RAIN_LEVEL = 1;
    public static final int MIN_RANDOMNESS = 0;
    public static final int MAX_RANDOMNESS = 3; // randomness above 3 might produce unexpected results

    private Integer ambientTemperature;
    private Double cloudLevel;
    private Double rainLevel;
    private Integer randomness;
}
