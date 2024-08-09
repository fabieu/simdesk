package de.sustineo.simdesk.configuration;

public class Reference {
    // Static references
    public static final String SIMDESK = "https://simdesk.eu";
    public static final String SIMDESK_CREDITS = SIMDESK + "/credits";
    public static final String GITHUB = "https://github.com/fabieu/simdesk";
    public static final String GITHUB_DISCUSSIONS = GITHUB + "/discussions";
    public static final String SUSTINEO = "https://sustineo.de";

    // Dynamic reference templates
    private static final String GITHUB_RELEASE_TEMPLATE = GITHUB + "/releases/tag/%s";

    public static String getGitHubRelease(String tag) {
        return String.format(GITHUB_RELEASE_TEMPLATE, tag);
    }
}
