package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.MemberData;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class DiscordService {
    private static final String DISCORD_API_BASE_URL = "https://discord.com/api/v10";

    private final RestTemplate restTemplate;

    @Getter
    private final Long guildId;

    public DiscordService(@Qualifier("discord") RestTemplate restTemplate,
                          @Value("${simdesk.auth.discord.guild-id}") String guildId) {
        this.restTemplate = restTemplate;
        this.guildId = Long.parseLong(guildId);
    }

    public MemberData getMemberData(String accessToken) {
        String url = String.format("%s/users/@me/guilds/%s/member ", DISCORD_API_BASE_URL, guildId);
        ResponseEntity<MemberData> response = restTemplate.exchange(url, HttpMethod.GET, defaultEntity(accessToken), MemberData.class);

        return response.getBody();
    }

    public Set<Long> getMemberRoleIds(String accessToken) {
        MemberData memberData = getMemberData(accessToken);

        if (memberData == null) {
            return Collections.emptySet();
        }

        return memberData.roles().stream()
                .map(Id::asLong)
                .collect(Collectors.toSet());
    }

    private HttpEntity<?> defaultEntity(String authToken) {
        return new HttpEntity<>(defaultHeadersWithBearer(authToken));
    }

    private HttpHeaders defaultHeadersWithBearer(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return headers;
    }
}
