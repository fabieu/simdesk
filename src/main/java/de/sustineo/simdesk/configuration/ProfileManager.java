package de.sustineo.simdesk.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ProfileManager {
    public static final String PROFILE_LEADERBOARD = "acc-leaderboard";
    public static final String PROFILE_ENTRYLIST = "acc-entrylist";
    public static final String PROFILE_BOP = "acc-bop";
    public static final String PROFILE_DISCORD = "discord";
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

    public static boolean isBopProfileEnabled() {
        return isActive(PROFILE_BOP);
    }

    public static boolean isDiscordProfileEnabled() {
        return isActive(PROFILE_DISCORD);
    }

    public static boolean isOAuth2ProfileEnabled() {
        return isDiscordProfileEnabled();
    }
}
