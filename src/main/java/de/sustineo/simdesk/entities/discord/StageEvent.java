package de.sustineo.simdesk.entities.discord;

import discord4j.core.object.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StageEvent {
    private Member member;
    private StageEventType type;
    private Instant timestamp;
}