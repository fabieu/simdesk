package de.sustineo.acc.servertools.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ProfileManager {
    public static final String PROFILE_LEADERBOARD = "acc-leaderboard";
    public static final String PROFILE_ENTRYLIST = "acc-entrylist";
    public static final String PROFILE_WEATHER = "acc-weather";
    public static final String PROFILE_RACEAPP = "acc-raceapp";
    private static List<String> activeProfiles = new ArrayList<>();

    public ProfileManager(Environment environment) {
        activeProfiles = List.of(environment.getActiveProfiles());
    }

    public static boolean isActive(String profile) {
        return activeProfiles.contains(profile);
    }

    public static boolean isLeaderboardProfileEnabled() {
        return isActive(PROFILE_LEADERBOARD);
    }

    public static boolean isEntrylistProfileEnabled() {
        return isActive(PROFILE_ENTRYLIST);
    }

    public static boolean isWeatherProfileEnabled() {
        return isActive(PROFILE_WEATHER);
    }
}
