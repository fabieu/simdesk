package de.sustineo.simdesk.services;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.MemberData;
import discord4j.discordjson.json.RoleData;
import discord4j.rest.http.client.ClientException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class DiscordService {
    private final String guildId;
    private final DiscordClient botClient;
    private final GatewayDiscordClient gatewayClient;

    public DiscordService(@Value("${simdesk.auth.discord.token}") String discordApplicationToken,
                          @Value("${simdesk.auth.discord.guild-id}") String guildId) {
        this.botClient = DiscordClientBuilder.create(discordApplicationToken).build();
        this.gatewayClient = botClient.login().block();
        this.guildId = guildId;

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
