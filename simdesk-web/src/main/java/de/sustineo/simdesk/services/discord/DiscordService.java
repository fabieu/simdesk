package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.MemberData;
import discord4j.rest.http.client.ClientException;
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
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log
@Getter
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class DiscordService {
    private static final String DISCORD_API_BASE_URL = "https://discord.com/api/v10";

    private final GatewayDiscordClient client;
    private final Snowflake guildId;

    private final RestTemplate restTemplate;

    public DiscordService(GatewayDiscordClient gatewayDiscordClient,
                          @Value("${simdesk.auth.discord.guild-id}") String guildId,
                          @Qualifier("discord") RestTemplate restTemplate) {
        this.client = gatewayDiscordClient;
        this.guildId = Snowflake.of(guildId);

        this.restTemplate = restTemplate;
    }

    public List<Role> getGuildRoles() throws ClientException {
        return client.getGuildById(guildId)
                .map(Guild::getRoles)
                .flatMapMany(Flux::collectList)
                .blockLast();
    }

    public Map<Snowflake, Role> getGuildRoleMap() throws ClientException {
        return getGuildRoles().stream()
                .collect(Collectors.toMap(Role::getId, Function.identity()));
    }

    //  ### Legacy OAuth2 authentication without Discord Bot usage ###

    // Leverages Discord's REST API to get member data for the user associated with the provided access token
    public MemberData getMemberData(String accessToken) {
        String url = String.format("%s/users/@me/guilds/%s/member ", DISCORD_API_BASE_URL, guildId);
        ResponseEntity<MemberData> response = restTemplate.exchange(url, HttpMethod.GET, defaultEntity(accessToken), MemberData.class);

        return response.getBody();
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

    private HttpEntity<?> defaultEntity(String authToken) {
        return new HttpEntity<>(defaultHeadersWithBearer(authToken));
    }

    private HttpHeaders defaultHeadersWithBearer(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return headers;
    }
}
