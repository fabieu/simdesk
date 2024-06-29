package de.sustineo.simdesk.entities.discord;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

public interface Command {
    void execute(ChatInputInteractionEvent event);
}
