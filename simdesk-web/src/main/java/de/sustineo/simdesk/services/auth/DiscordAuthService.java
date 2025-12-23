package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.MemberData;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Getter
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class DiscordAuthService {
    private static final String DISCORD_API_BASE_URL = "https://discord.com/api";

    private final Snowflake guildId;
    private final RestClient restClient;

    public DiscordAuthService(@Value("${simdesk.auth.discord.guild-id}") String guildId,
                              @Qualifier("discord") RestClient restClient) {
        this.guildId = Snowflake.of(guildId);
        this.restClient = restClient;
    }

    public MemberData getMemberData(String accessToken) {
        return restClient.get()
                .uri(String.format("%s/users/@me/guilds/%s/member", DISCORD_API_BASE_URL, guildId.asString()))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(MemberData.class);
    }

    public Set<String> getMemberRoleIds(String accessToken) {
        MemberData memberData = getMemberData(accessToken);

        if (memberData == null) {
            return Collections.emptySet();
        }

        return memberData.roles().stream()
                .map(Id::asString)
                .collect(Collectors.toSet());
    }
}
