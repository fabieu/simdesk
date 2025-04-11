package de.sustineo.simdesk.entities.output;

import de.sustineo.simdesk.entities.json.kunos.acc.AccWeatherSettings;
import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeatherSettingsOutput {
    private AccWeatherSettings accSettings;
    private OpenWeatherModel weatherModel;
}
