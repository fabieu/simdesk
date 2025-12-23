package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.MemberData;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
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
    private static final String DISCORD_API_BASE_URL = "https://discord.com/api/v10";

    private final String guildId;
    private final RestClient restClient;

    public DiscordAuthService(@NonNull @Value("${simdesk.auth.discord.guild-id}") String guildId,
                              @Qualifier("discord") RestClient restClient) {
        this.guildId = guildId;
        this.restClient = restClient;
    }

    public MemberData getMemberData(String accessToken) {
        return restClient.get()
                .uri(String.format("%s/users/@me/guilds/%s/member", DISCORD_API_BASE_URL, guildId))
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
