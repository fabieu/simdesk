package de.sustineo.acc.servertools.services;

import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.json.discord.Guild;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.MemberData;
import discord4j.discordjson.json.RoleData;
import discord4j.rest.http.client.ClientException;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class DiscordService {
    private static final String DISCORD_API_BASE_URL = "https://discord.com/api";
    private static final String DISCORD_API_VERSION = "10";
    private static final String DISCORD_API_ENDPOINT = String.format("%s/v%s", DISCORD_API_BASE_URL, DISCORD_API_VERSION);
    private final BuildProperties buildProperties;
    @Getter
    private final String guildId;
    private final DiscordClient botClient;
    private final GatewayDiscordClient gatewayClient;
    private final RestClient oauthClient;

    public DiscordService(@Value("${auth.discord.token}") String discordApplicationToken,
                          @Value("${auth.discord.guild-id}") String guildId,
                          BuildProperties buildProperties) {
        this.botClient = DiscordClientBuilder.create(discordApplicationToken).build();
        this.gatewayClient = botClient.login().block();
        this.guildId = guildId;
        this.buildProperties = buildProperties;
        this.oauthClient = createOAuthClient();

        // Check if the Discord bot is connected to the guild
        checkIfBotIsConnectedToGuild();
    }

    private void checkIfBotIsConnectedToGuild() {
        try {
            botClient.getGuildService()
                    .getGuild(Long.parseLong(guildId))
                    .block();
        } catch (ClientException e) {
            String message = String.format("Discord bot is not connected to the guild with ID %s. Consider inviting the bot to the guild.", guildId);
            log.severe(message);
            throw new IllegalStateException(message, e);
        }
    }

    private RestClient createOAuthClient() {
        return RestClient.builder()
                .baseUrl(DISCORD_API_ENDPOINT)
                .defaultHeader("User-Agent", String.format("DiscordBot (%s, %s)", buildProperties.getName(), buildProperties.getVersion()))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @NonNull
    public List<Guild> getGuildsOfCurrentUser(String bearerToken) {
        Guild[] guildsOfUser = oauthClient.get()
                .uri("/users/@me/guilds")
                .header("Authorization", String.format("Bearer %s", bearerToken))
                .retrieve()
                .body(Guild[].class);

        return Optional.ofNullable(guildsOfUser)
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

    public List<RoleData> getRolesOfGuild() throws ClientException {
        return botClient.getGuildService()
                .getGuildRoles(Long.parseLong(guildId))
                .collectList()
                .block();
    }

    public MemberData getMemberOfGuild(long memberId) throws ClientException {
        return botClient.getGuildService()
                .getGuildMember(Long.parseLong(guildId), memberId)
                .block();
    }

    @NonNull
    public List<RoleData> getRolesOfMember(long memberId) throws ClientException {
        List<RoleData> guildRoles = getRolesOfGuild();
        MemberData memberData = getMemberOfGuild(memberId);

        return guildRoles.stream()
                .filter(role -> memberData.roles().contains(role.id()))
                .collect(Collectors.toList());
    }
}
