package de.sustineo.simdesk.entities.json.kunos.acc;

import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccWeatherSettings {
    private OpenWeatherModel weatherModel;
    private Integer ambientTemperature;
    private Double cloudLevel;
    private Double rainLevel;
    private Integer randomness;
}
