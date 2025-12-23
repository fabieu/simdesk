package de.sustineo.simdesk.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class SpringProfile {
    public static final String STEWARDING = "stewarding";
    public static final String LEADERBOARD = "acc-leaderboard";
    public static final String ENTRYLIST = "acc-entrylist";
    public static final String BOP = "acc-bop";
    public static final String MAP = "map";
    public static final String DISCORD = "discord";
    public static final String ANALYTICS = "analytics";
    public static final String DEBUG = "debug";

    private static Environment environment;

    public SpringProfile(Environment environment) {
        SpringProfile.environment = environment;
    }

    private static boolean isActive(String profile) {
        return List.of(environment.getActiveProfiles()).contains(profile);
    }

    public static boolean isDebug() {
        return isActive(DEBUG);
    }

    public static boolean isStewardingEnabled() {
        return isActive(STEWARDING);
    }

    public static boolean isLeaderboardEnabled() {
        return isActive(LEADERBOARD);
    }

    public static boolean isEntrylistEnabled() {
        return isActive(ENTRYLIST);
    }

    public static boolean isBopEnabled() {
        return isActive(BOP);
    }

    public static boolean isMapEnabled() {
        return isActive(MAP);
    }

    public static boolean isDiscordEnabled() {
        return isActive(DISCORD);
    }

    public static boolean isOAuth2Enabled() {
        return isDiscordEnabled();
    }
}
