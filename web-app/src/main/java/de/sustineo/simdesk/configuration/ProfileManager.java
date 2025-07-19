package de.sustineo.simdesk.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class ProfileManager {
    public static final String PROFILE_LEADERBOARD = "acc-leaderboard";
    public static final String PROFILE_ENTRYLIST = "acc-entrylist";
    public static final String PROFILE_BOP = "acc-bop";
    public static final String PROFILE_MAP = "map";
    public static final String PROFILE_LIVE_TIMING = "acc-live-timing";
    public static final String PROFILE_DISCORD = "discord";
    public static final String PROFILE_DEBUG = "debug";
    private static Environment environment;

    public ProfileManager(Environment environment) {
        ProfileManager.environment = environment;
    }

    private static boolean isActive(String profile) {
        return List.of(environment.getActiveProfiles()).contains(profile);
    }

    public static boolean isLeaderboardProfileEnabled() {
        return isActive(PROFILE_LEADERBOARD);
    }

    public static boolean isEntrylistProfileEnabled() {
        return isActive(PROFILE_ENTRYLIST);
    }

    public static boolean isBopProfileEnabled() {
        return isActive(PROFILE_BOP);
    }

    public static boolean isMapProfileEnabled() {
        return isActive(PROFILE_MAP);
    }

    public static boolean isLiveTimingProfileEnabled() {
        return isActive(PROFILE_LIVE_TIMING);
    }

    public static boolean isDiscordProfileEnabled() {
        return isActive(PROFILE_DISCORD);
    }

    public static boolean isOAuth2ProfileEnabled() {
        return isDiscordProfileEnabled();
    }

    public static boolean isDebug() {
        return isActive(PROFILE_DEBUG);
    }
}
