package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.MemberData;
import discord4j.discordjson.json.RoleData;
import discord4j.rest.http.client.ClientException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class DiscordService {
    private final StageAttendanceService stageAttendanceService;

    private final Long guildId;
    private final DiscordClient botClient;

    public DiscordService(@Value("${simdesk.auth.discord.token}") String discordApplicationToken,
                          @Value("${simdesk.auth.discord.guild-id}") String guildId,
                          StageAttendanceService stageAttendanceService) {
        this.stageAttendanceService = stageAttendanceService;

        this.guildId = Long.parseLong(guildId);
        this.botClient = DiscordClient.create(discordApplicationToken);
        botClient
                .withGateway(client -> {
                    handleGatewayEvents(client);
                    return client.onDisconnect();
                })
                .block();


        // Check if the Discord bot is connected to the guild
        checkIfBotIsConnectedToGuild();
    }

    private void checkIfBotIsConnectedToGuild() {
        try {
            botClient.getGuildById(Snowflake.of(guildId))
                    .getSelfMember()
                    .block();
        } catch (ClientException e) {
            String message = String.format("Discord bot is not connected to the guild with ID %s. Consider inviting the bot to the guild.", guildId);
            log.severe(message);
            throw new IllegalStateException(message, e);
        }
    }

    public List<MemberData> getMembersOfGuild() {
        return botClient.getGuildById(Snowflake.of(guildId))
                .getMembers()
                .collectList()
                .block();
    }

    public MemberData getMemberOfGuild(long memberId) throws ClientException {
        return botClient.getGuildById(Snowflake.of(guildId))
                .getMember(Snowflake.of(memberId))
                .block();
    }

    public List<RoleData> getRolesOfGuild() throws ClientException {
        return botClient.getGuildById(Snowflake.of(guildId))
                .getRoles()
                .collectList()
                .block();
    }

    public Map<Long, String> getRolesOfGuildMap() {
        List<RoleData> roles = getRolesOfGuild();

        Map<Long, String> rolesMap = new HashMap<>();
        for (RoleData role : roles) {
            rolesMap.put(role.id().asLong(), role.name());
        }

        return rolesMap;
    }

    public List<RoleData> getRolesOfMember(long memberId) throws ClientException {
        List<RoleData> guildRoles = getRolesOfGuild();
        MemberData memberData = getMemberOfGuild(memberId);

        return guildRoles.stream()
                .filter(role -> memberData.roles().contains(role.id()))
                .collect(Collectors.toList());
    }

    public void handleGatewayEvents(GatewayDiscordClient client) {
        client.on(MessageCreateEvent.class).subscribe(event -> {
            Instant receivedAt = Instant.now();

            // Ignore events from other guilds
            Optional<Snowflake> guildId = event.getGuildId();
            if (guildId.isEmpty() || !Snowflake.of(this.guildId).equals(guildId.get())) {
                return;
            }

            Message message = event.getMessage();
            if (message.getContent().equalsIgnoreCase("!ping")) {
                message.getChannel().block().createMessage("Pong!").block();
            }

            if (Message.Type.STAGE_START.equals(message.getType())) {
                stageAttendanceService.handleStageStartEvent(event, receivedAt);
            } else if (Message.Type.STAGE_END.equals(message.getType())) {
                stageAttendanceService.handleStageEndEvent(event, receivedAt);
            }
        });

        client.on(VoiceStateUpdateEvent.class).subscribe(event -> {
            Instant receivedAt = Instant.now();

            // Ignore events from other guilds
            Snowflake guildId = event.getCurrent().getGuildId();
            if (!Snowflake.of(this.guildId).equals(guildId)) {
                return;
            }

            if (event.isJoinEvent() && isStageChannel(event)) {
                stageAttendanceService.handleStageJoinEvent(event, receivedAt);
            } else if (event.isLeaveEvent() && isStageChannel(event)) {
                stageAttendanceService.handleStageLeaveEvent(event, receivedAt);
            }
        });
    }

    /**
     * Check if the event is related to a stage channel.
     *
     * @param event The event to check.
     * @return True if the event is related to a stage channel, false otherwise.
     */
    private boolean isStageChannel(VoiceStateUpdateEvent event) {
        if (event.isJoinEvent() || event.isMoveEvent()) {
            return event.getCurrent().getChannel().blockOptional()
                    .map(channel -> Channel.Type.GUILD_STAGE_VOICE.equals(channel.getType()))
                    .orElse(false);
        } else if (event.isLeaveEvent()) {
            return event.getOld().map(voiceState -> voiceState.getChannel().block())
                    .map(channel -> Channel.Type.GUILD_STAGE_VOICE.equals(channel.getType()))
                    .orElse(false);

        }

        return false;
    }
}
