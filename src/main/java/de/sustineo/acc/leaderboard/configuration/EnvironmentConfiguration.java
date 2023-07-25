package de.sustineo.acc.leaderboard.configuration;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EnvironmentConfiguration {
    private static Map<String, String> environmentVariables = new HashMap<>();

    public EnvironmentConfiguration() {
        environmentVariables = System.getenv();
    }

    public static String getCommunityName() {
        return environmentVariables.getOrDefault("LEADERBOARD_COMMUNITY_NAME", "ACC");
    }
}
