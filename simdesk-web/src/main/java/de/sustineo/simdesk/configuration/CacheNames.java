package de.sustineo.simdesk.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNames {
    public static final String API_KEY = "api-key";
    public static final String SETTINGS = "settings";
    public static final String BOPS = "bops";
    public static final String BOP_PROVIDERS = "bop-providers";
    public static final String SESSION = "session";
    public static final String SESSIONS = "sessions";
    public static final String LAPS_SESSION = "lap-session";
    public static final String LAPS_DRIVER = "lap-driver";
    public static final String RANKINGS = "rankings";
    public static final String LEADERBOARD_LINES = "leaderboard-lines";

    public static final String[] ALL = {
            API_KEY,
            SETTINGS,
            BOPS,
            BOP_PROVIDERS,
            SESSION,
            SESSIONS,
            LAPS_SESSION,
            LAPS_DRIVER,
            RANKINGS,
            LEADERBOARD_LINES
    };
}
