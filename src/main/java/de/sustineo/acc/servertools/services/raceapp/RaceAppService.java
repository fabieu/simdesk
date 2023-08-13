package de.sustineo.acc.servertools.services.raceapp;

import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.raceapp.RaceAppSeries;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Profile(ProfileManager.PROFILE_RACEAPP)
@Service
@Log
public class RaceAppService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://raceapp.eu/api";
    private static final String SERIES_ENDPOINT_TEMPLATE = BASE_URL + "/series/%s";

    public RaceAppSeries fetchSeries(int seriesId) {
        ResponseEntity<RaceAppSeries> response = restTemplate.getForEntity(String.format(SERIES_ENDPOINT_TEMPLATE, seriesId), RaceAppSeries.class);
        return response.getBody();
    }
}
