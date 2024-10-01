package de.sustineo.simdesk.configuration;

public class Reference {
    public static final String SIMDESK = "https://simdesk.eu";
    public static final String SIMDESK_CREDITS = SIMDESK + "/credits";
    public static final String GITHUB = "https://github.com/fabieu/simdesk";
    public static final String GITHUB_DISCUSSIONS = GITHUB + "/discussions";

    public static String getGitHubRelease(String tag) {
        return String.format(GITHUB + "/releases/tag/%s", tag);
    }
}
