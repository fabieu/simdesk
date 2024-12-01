package de.sustineo.simdesk.services.weather;


import de.sustineo.simdesk.configuration.ProfileManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(ProfileManager.PROFILE_MAP)
@Service
public class WeatherService {
    @Getter
    private final String openweathermapApiKey;

    public WeatherService(@Value("${simdesk.openweathermap.api-key}") String openweathermapApiKey) {
        this.openweathermapApiKey = openweathermapApiKey;
    }

    public boolean isReady() {
        return openweathermapApiKey != null && !openweathermapApiKey.isEmpty();
    }
}
