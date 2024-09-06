package de.sustineo.simdesk.services.discord;

import discord4j.common.util.Snowflake;

public class DiscordUtils {
    public static String getChannelMention(Snowflake channelId) {
        return "<#" + channelId.asString() + '>';
    }

    public static String getUserMention(Snowflake userId) {
        return "<@" + userId.asString() + '>';
    }
}
