package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.discord.StageEvent;
import de.sustineo.simdesk.entities.discord.StageEventType;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class StageAttendanceService {
    private static final HashMap<Long, List<StageEvent>> stageEvents = new HashMap<>();

    public void handleStageStartEvent(MessageCreateEvent event, Instant receivedAt) {
        log.info("Handling stage start event: " + event);
        log.info(stageEvents.toString());
    }

    public void handleStageEndEvent(MessageCreateEvent event, Instant receivedAt) {
        log.info("Handling stage end event: " + event);
        log.info(stageEvents.toString());
    }

    public void handleStageJoinEvent(VoiceStateUpdateEvent event, Instant receivedAt) {
        long channelId = event.getCurrent().getChannelId()
                .map(Snowflake::asLong)
                .orElseThrow();

        StageEvent stageEvent = StageEvent.builder()
                .member(event.getCurrent().getMember().block())
                .type(StageEventType.JOIN)
                .timestamp(receivedAt)
                .build();

        addStageEvent(channelId, stageEvent);
    }

    public void handleStageLeaveEvent(VoiceStateUpdateEvent event, Instant receivedAt) {
        long channelId = event.getOld()
                .map(VoiceState::getChannelId)
                .map(Optional::orElseThrow)
                .map(Snowflake::asLong)
                .orElseThrow();

        Member member = event.getOld()
                .map(voiceState -> voiceState.getMember().block())
                .orElseThrow();

        StageEvent stageEvent = StageEvent.builder()
                .member(member)
                .type(StageEventType.LEAVE)
                .timestamp(receivedAt)
                .build();

        addStageEvent(channelId, stageEvent);
    }

    private void addStageEvent(long channelId, StageEvent stageEvent) {
        List<StageEvent> events = stageEvents.getOrDefault(channelId, new ArrayList<>());
        events.add(stageEvent);
        stageEvents.put(channelId, events);
    }
}
