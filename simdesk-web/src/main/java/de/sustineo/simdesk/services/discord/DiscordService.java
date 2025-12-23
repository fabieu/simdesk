package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.http.client.ClientException;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log
@Getter
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class DiscordService {
    private final GatewayDiscordClient client;
    private final Snowflake guildId;

    public DiscordService(@Value("${simdesk.auth.discord.guild-id}") String guildId,
                          @Value("${simdesk.auth.discord.token}") String token) {
        this.guildId = Snowflake.of(guildId);
        this.client = DiscordClientBuilder.create(token).build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online())
                .setEnabledIntents(IntentSet.of(Intent.GUILD_MESSAGES))
                .login()
                .block();
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

    public List<GuildChannel> getGuildChannels() {
        return client.getGuildById(guildId)
                .map(Guild::getChannels)
                .flatMapMany(Flux::collectList)
                .blockLast();
    }

    public List<GuildChannel> getGuildTextChannels() {
        return getGuildChannels().stream()
                .filter(channel -> Channel.Type.GUILD_TEXT.equals(channel.getType()))
                .toList();
    }

    public void sendMessage(Snowflake channelId, MessageCreateSpec messageCreateSpec) {
        client.getChannelById(channelId)
                .ofType(GuildMessageChannel.class)
                .flatMap(channel -> channel.createMessage(messageCreateSpec))
                .subscribe();
    }
}
