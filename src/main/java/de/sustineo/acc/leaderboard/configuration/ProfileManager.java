package de.sustineo.acc.leaderboard.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class ProfileManager {
    public static final String PROFILE_H2 = "h2";
    public static final String PROFILE_FILE_INTEGRATION = "file-integration";
    public static final String PROFILE_DEVELOPMENT = "development";
    private final List<String> activeProfiles;

    public ProfileManager(Environment environment) {
        this.activeProfiles = List.of(environment.getActiveProfiles());
    }

    public boolean isActive(String profile) {
        return activeProfiles.contains(profile);
    }
}
