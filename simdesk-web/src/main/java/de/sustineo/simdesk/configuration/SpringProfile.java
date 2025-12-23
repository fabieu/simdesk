package de.sustineo.simdesk.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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

    private static boolean matchesProfiles(String profileExpression) {
        return environment.matchesProfiles(profileExpression);
    }

    public static boolean isDebug() {
        return matchesProfiles(DEBUG);
    }

    public static boolean isStewardingEnabled() {
        return matchesProfiles(STEWARDING);
    }

    public static boolean isLeaderboardEnabled() {
        return matchesProfiles(LEADERBOARD);
    }

    public static boolean isEntrylistEnabled() {
        return matchesProfiles(ENTRYLIST);
    }

    public static boolean isBopEnabled() {
        return matchesProfiles(BOP);
    }

    public static boolean isMapEnabled() {
        return matchesProfiles(MAP);
    }

    public static boolean isDiscordEnabled() {
        return matchesProfiles(DISCORD);
    }

    public static boolean isOAuth2Enabled() {
        return isDiscordEnabled();
    }
}
