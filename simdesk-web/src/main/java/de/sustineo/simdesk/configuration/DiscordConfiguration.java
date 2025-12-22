package de.sustineo.simdesk.configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(ProfileManager.PROFILE_DISCORD)
public class DiscordConfiguration {
    @Bean
    public GatewayDiscordClient gatewayDiscordClient(@Value("${simdesk.auth.discord.token}") String token) {
        return DiscordClientBuilder.create(token).build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online())
                .setEnabledIntents(IntentSet.of(Intent.GUILD_MESSAGES))
                .login()
                .block();
    }
}
