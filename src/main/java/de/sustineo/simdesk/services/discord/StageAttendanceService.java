package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.discord.Command;
import de.sustineo.simdesk.entities.discord.StageAttendanceEvent;
import de.sustineo.simdesk.entities.discord.StageAttendanceRange;
import de.sustineo.simdesk.entities.discord.StageEventType;
import de.sustineo.simdesk.services.PropertyService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.java.Log;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class StageAttendanceService {
    private static final String COMMAND_REPORT_CHANNEL = "report-channel";
    public static final String PROPERTY_REPORT_CHANNEL_ID = "discord.reports.channel-id";
    private static final HashMap<Long, Instant> stageStartTimestamps = new HashMap<>();
    private static final HashMap<Long, List<StageAttendanceEvent>> stageAttendanceEvents = new HashMap<>();

    private final DiscordService discordService;
    private final PropertyService propertyService;

    public StageAttendanceService(@Lazy DiscordService discordService,
                                  PropertyService propertyService) {
        this.discordService = discordService;
        this.propertyService = propertyService;
    }

    public void registerCommands(List<ApplicationCommandRequest> applicationCommandRequests, Map<String, Command> commandMap) {
        ApplicationCommandOptionData optionChannel = ApplicationCommandOptionData.builder()
                .name("channel")
                .description("Define the channel where reports will be sent to")
                .type(ApplicationCommandOption.Type.CHANNEL.getValue())
                .required(false)
                .build();

        ApplicationCommandRequest applicationCommand = ApplicationCommandRequest.builder()
                .name(COMMAND_REPORT_CHANNEL)
                .description("Modify channel for reports sent by the bot")
                .addOption(optionChannel)
                .build();
        applicationCommandRequests.add(applicationCommand);

        commandMap.put(COMMAND_REPORT_CHANNEL, event -> {
            if (!Snowflake.of(discordService.getGuildId()).equals(event.getInteraction().getGuildId().orElse(null))) {
                return;
            }

            Optional<ApplicationCommandInteraction> commandInteraction = event.getInteraction().getCommandInteraction();

            Optional<ApplicationCommandInteractionOption> commandInteractionChannel = commandInteraction
                    .flatMap(applicationCommandInteraction -> applicationCommandInteraction.getOption(optionChannel.name()));

            if (commandInteractionChannel.isPresent() && commandInteractionChannel.get().getValue().isPresent()) {
                Snowflake reportChannelId = commandInteractionChannel.get().getValue().get().asSnowflake();
                propertyService.setPropertyValue(StageAttendanceService.PROPERTY_REPORT_CHANNEL_ID, reportChannelId.asString());
                event.reply(String.format("Successfully changed report channel to %s", DiscordUtils.getChannelMention(reportChannelId))).subscribe();
            } else {
                String reportChannelId = propertyService.getPropertyValue(StageAttendanceService.PROPERTY_REPORT_CHANNEL_ID);
                String message = reportChannelId == null ? "No report channel set!" : String.format("Current report channel is %s", DiscordUtils.getChannelMention(Snowflake.of(reportChannelId)));
                event.reply(message).subscribe();
            }
        });
    }

    public void handleStageStartEvent(MessageCreateEvent event, Instant receivedAt) {
        log.fine("Handling stage start event: " + event);

        long channelId = event.getMessage().getChannelId().asLong();

        stageStartTimestamps.put(channelId, receivedAt);
    }

    public void handleStageEndEvent(MessageCreateEvent event, Instant receivedAt) {
        log.fine("Handling stage end event: " + event);

        long channelId = event.getMessage().getChannelId().asLong();

        Instant stageStartTimestamp = stageStartTimestamps.get(channelId);
        Instant stageEndTimestamp = receivedAt;

        if (stageAttendanceEvents.get(channelId) == null) {
            clearInternalStageState(channelId);
            return;
        }

        // Persist attending members by member ID
        Map<Long, Member> membersById = stageAttendanceEvents.get(channelId).stream()
                .map(StageAttendanceEvent::getMember)
                .collect(Collectors.toMap(member -> member.getId().asLong(), member -> member, (a, b) -> a));

        // Group stage join and leave events by member ID
        Map<Long, List<StageAttendanceEvent>> stageJoinEventsByMemberId = stageAttendanceEvents.get(channelId).stream()
                .filter(stageAttendanceEvent -> stageAttendanceEvent.getType() == StageEventType.JOIN)
                .collect(Collectors.groupingBy(stageAttendanceEvent -> stageAttendanceEvent.getMember().getId().asLong()));

        Map<Long, List<StageAttendanceEvent>> stageLeaveEventsByMemberId = stageAttendanceEvents.get(channelId).stream()
                .filter(stageAttendanceEvent -> stageAttendanceEvent.getType() == StageEventType.LEAVE)
                .collect(Collectors.groupingBy(stageAttendanceEvent -> stageAttendanceEvent.getMember().getId().asLong()));

        // Calculate stage attendance ranges for each member based on join and leave events
        Map<Member, List<StageAttendanceRange>> stageAttendanceRangeByMember = new HashMap<>();
        for (Map.Entry<Long, List<StageAttendanceEvent>> stageAttendanceJoinEntry : stageJoinEventsByMemberId.entrySet()) {
            long memberId = stageAttendanceJoinEntry.getKey();
            List<StageAttendanceEvent> joinEvents = stageAttendanceJoinEntry.getValue();
            List<StageAttendanceEvent> leaveEvents = stageLeaveEventsByMemberId.getOrDefault(memberId, new ArrayList<>());

            List<StageAttendanceRange> stageAttendanceRanges = new ArrayList<>();
            for (StageAttendanceEvent joinEvent : joinEvents) {
                Instant joinTimestamp = joinEvent.getTimestamp();
                Instant correctedJoinTimestamp = joinTimestamp.isBefore(stageStartTimestamp) ? stageStartTimestamp : joinTimestamp;

                Optional<StageAttendanceEvent> leaveEvent = leaveEvents.stream()
                        .filter(leave -> leave.getTimestamp().isAfter(joinTimestamp))
                        .findFirst();
                Instant leaveTimestamp = leaveEvent
                        .map(StageAttendanceEvent::getTimestamp)
                        .orElse(stageEndTimestamp);

                StageAttendanceRange stageAttendanceRange = StageAttendanceRange.builder()
                        .joinTimestamp(correctedJoinTimestamp)
                        .leaveTimestamp(leaveTimestamp)
                        .build();

                if (stageAttendanceRange.getLeaveTimestamp().isAfter(stageStartTimestamp)) {
                    stageAttendanceRanges.add(stageAttendanceRange);
                }

                leaveEvent.ifPresent(leaveEvents::remove);
            }

            stageAttendanceRangeByMember.put(membersById.get(memberId), stageAttendanceRanges);
        }

        // Send stage attendance report
        String reportChannelId = propertyService.getPropertyValue(PROPERTY_REPORT_CHANNEL_ID);
        if (reportChannelId != null) {
            sendAttendanceReport(Snowflake.of(reportChannelId), stageAttendanceRangeByMember, stageStartTimestamp, stageEndTimestamp);
        } else {
            log.severe("Could not send stage attendance report, because report channel is not set");
        }

        clearInternalStageState(channelId);
    }

    private void clearInternalStageState(long channelId) {
        stageStartTimestamps.remove(channelId);
        stageAttendanceEvents.remove(channelId);
    }

    private void sendAttendanceReport(Snowflake channelId, Map<Member, List<StageAttendanceRange>> stageAttendanceRangeByMember, Instant stageStartTimestamp, Instant stageEndTimestamp) {
        final String durationFormat = "HH:mm:ss";

        StringBuilder content = new StringBuilder();
        content.append("## Stage attendance report").append("\n");
        content.append("**Details:**").append("\n");
        content.append(String.format("* <t:%s:f> - <t:%s:f> (Duration: %s)",
                stageStartTimestamp.getEpochSecond(),
                stageEndTimestamp.getEpochSecond(),
                DurationFormatUtils.formatDuration(Duration.between(stageStartTimestamp, stageEndTimestamp).toMillis(), durationFormat, true)));
        content.append("\n\n");

        content.append("**Participants:**").append("\n");
        for (Map.Entry<Member, List<StageAttendanceRange>> entry : stageAttendanceRangeByMember.entrySet()) {
            Member member = entry.getKey();
            List<StageAttendanceRange> stageAttendanceRanges = entry.getValue();

            String stageAttendanceRangeContent = stageAttendanceRanges.stream()
                    .map(stageAttendanceRange -> String.format("<t:%s:T> - <t:%s:T>", stageAttendanceRange.getJoinTimestamp().getEpochSecond(), stageAttendanceRange.getLeaveTimestamp().getEpochSecond()))
                    .collect(Collectors.joining(", "));
            long stageAttendanceDuration = stageAttendanceRanges.stream()
                    .mapToLong(stageAttendanceRange -> Duration.between(stageAttendanceRange.getJoinTimestamp(), stageAttendanceRange.getLeaveTimestamp()).toMillis())
                    .sum();

            content.append(String.format("%s: %s (Duration: %s)", member.getMention(), stageAttendanceRangeContent, DurationFormatUtils.formatDuration(stageAttendanceDuration, durationFormat, true))).append("\n");
        }

        discordService.sendMessage(channelId, content.toString());
    }

    public void handleStageJoinEvent(VoiceStateUpdateEvent event, Instant receivedAt) {
        log.fine("Handling stage join event: " + event);

        long channelId = event.getCurrent().getChannelId()
                .map(Snowflake::asLong)
                .orElseThrow();

        StageAttendanceEvent stageAttendanceEvent = StageAttendanceEvent.builder()
                .member(event.getCurrent().getMember().block())
                .type(StageEventType.JOIN)
                .timestamp(receivedAt)
                .build();

        addStageAttendanceEvent(channelId, stageAttendanceEvent);
    }

    public void handleStageLeaveEvent(VoiceStateUpdateEvent event, Instant receivedAt) {
        log.fine("Handling stage leave event: " + event);

        long channelId = event.getOld()
                .map(VoiceState::getChannelId)
                .map(Optional::orElseThrow)
                .map(Snowflake::asLong)
                .orElseThrow();

        Member member = event.getOld()
                .map(voiceState -> voiceState.getMember().block())
                .orElseThrow();

        StageAttendanceEvent stageAttendanceEvent = StageAttendanceEvent.builder()
                .member(member)
                .type(StageEventType.LEAVE)
                .timestamp(receivedAt)
                .build();

        addStageAttendanceEvent(channelId, stageAttendanceEvent);
    }

    private void addStageAttendanceEvent(long channelId, StageAttendanceEvent event) {
        List<StageAttendanceEvent> events = stageAttendanceEvents.getOrDefault(channelId, new ArrayList<>());
        events.add(event);
        stageAttendanceEvents.put(channelId, events);
    }
}
