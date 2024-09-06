package de.sustineo.simdesk.entities.discord;

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;

public interface Modal {
    void execute(ModalSubmitInteractionEvent event);
}
