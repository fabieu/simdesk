package de.sustineo.acc.leaderboard.configuration;

public class Environment {
    private static final String PREFIX = "LEADERBOARD_";
    public static final String COMMUNITY_NAME = System.getenv().getOrDefault(PREFIX + "COMMUNITY_NAME", "ACC");
}