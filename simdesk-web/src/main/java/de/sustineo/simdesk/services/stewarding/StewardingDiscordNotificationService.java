package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.*;
import de.sustineo.simdesk.mybatis.mapper.RaceWeekendMapper;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Log
@Profile(SpringProfile.STEWARDING)
@Service
public class StewardingDiscordNotificationService {
    private static final int COLOR_BLUE = 0x3498DB;
    private static final int COLOR_RED = 0xE74C3C;
    private static final int COLOR_GREEN = 0x2ECC71;
    private static final int COLOR_YELLOW = 0xF1C40F;
    private static final int COLOR_ORANGE = 0xE67E22;

    private final RaceWeekendMapper weekendMapper;
    private final RestClient restClient;

    public StewardingDiscordNotificationService(RaceWeekendMapper weekendMapper) {
        this.weekendMapper = weekendMapper;
        this.restClient = RestClient.create();
    }

    @Async
    public void sendIncidentNotification(Integer raceWeekendId, Incident incident) {
        String webhookUrl = getWebhookUrl(raceWeekendId);
        if (webhookUrl == null) {
            return;
        }

        Map<String, Object> embed = Map.of(
                "title", "🚩 New Incident Reported",
                "description", incident.getTitle() != null ? incident.getTitle() : "No title",
                "color", COLOR_BLUE,
                "fields", List.of(
                        Map.of("name", "Lap", "value", incident.getLap() != null ? String.valueOf(incident.getLap()) : "N/A", "inline", true),
                        Map.of("name", "Status", "value", incident.getStatus() != null ? incident.getStatus().getDescription() : "Reported", "inline", true)
                ),
                "timestamp", Instant.now().toString()
        );

        sendWebhook(webhookUrl, Map.of("embeds", List.of(embed)));
    }

    @Async
    public void sendDecisionNotification(Integer raceWeekendId, StewardDecision decision, Incident incident, String penaltyName) {
        String webhookUrl = getWebhookUrl(raceWeekendId);
        if (webhookUrl == null) {
            return;
        }

        boolean isNoAction = Boolean.TRUE.equals(decision.getIsNoAction());
        int color = isNoAction ? COLOR_GREEN : COLOR_RED;
        String title = isNoAction ? "✅ No Further Action" : "⚖️ Steward Decision";

        Map<String, Object> embed = Map.of(
                "title", title,
                "description", incident != null && incident.getTitle() != null ? incident.getTitle() : "Manual decision",
                "color", color,
                "fields", List.of(
                        Map.of("name", "Penalty", "value", penaltyName != null ? penaltyName : "No action", "inline", true),
                        Map.of("name", "Reasoning", "value", decision.getReasoning() != null ? decision.getReasoning() : "N/A", "inline", false)
                ),
                "timestamp", Instant.now().toString()
        );

        sendWebhook(webhookUrl, Map.of("embeds", List.of(embed)));
    }

    @Async
    public void sendAppealNotification(Integer raceWeekendId, Appeal appeal) {
        String webhookUrl = getWebhookUrl(raceWeekendId);
        if (webhookUrl == null) {
            return;
        }

        Map<String, Object> embed = Map.of(
                "title", "📋 Appeal Filed",
                "description", appeal.getReason() != null ? appeal.getReason() : "No reason provided",
                "color", COLOR_YELLOW,
                "fields", List.of(
                        Map.of("name", "Decision ID", "value", String.valueOf(appeal.getDecisionId()), "inline", true),
                        Map.of("name", "Status", "value", appeal.getStatus() != null ? appeal.getStatus().getDescription() : "Pending", "inline", true)
                ),
                "timestamp", Instant.now().toString()
        );

        sendWebhook(webhookUrl, Map.of("embeds", List.of(embed)));
    }

    @Async
    public void sendAppealReviewedNotification(Integer raceWeekendId, Appeal appeal) {
        String webhookUrl = getWebhookUrl(raceWeekendId);
        if (webhookUrl == null) {
            return;
        }

        int color = appeal.getStatus() == AppealStatus.ACCEPTED ? COLOR_GREEN : COLOR_RED;

        Map<String, Object> embed = Map.of(
                "title", "📋 Appeal Reviewed",
                "description", appeal.getResponse() != null ? appeal.getResponse() : "No response provided",
                "color", color,
                "fields", List.of(
                        Map.of("name", "Decision ID", "value", String.valueOf(appeal.getDecisionId()), "inline", true),
                        Map.of("name", "Result", "value", appeal.getStatus() != null ? appeal.getStatus().getDescription() : "N/A", "inline", true)
                ),
                "timestamp", Instant.now().toString()
        );

        sendWebhook(webhookUrl, Map.of("embeds", List.of(embed)));
    }

    @Async
    public void sendDecisionRevisedNotification(Integer raceWeekendId, StewardDecision oldDecision, StewardDecision newDecision) {
        String webhookUrl = getWebhookUrl(raceWeekendId);
        if (webhookUrl == null) {
            return;
        }

        Map<String, Object> embed = Map.of(
                "title", "🔄 Decision Revised",
                "description", "A previous steward decision has been revised.",
                "color", COLOR_ORANGE,
                "fields", List.of(
                        Map.of("name", "Previous Decision ID", "value", String.valueOf(oldDecision.getId()), "inline", true),
                        Map.of("name", "New Decision ID", "value", String.valueOf(newDecision.getId()), "inline", true),
                        Map.of("name", "New Reasoning", "value", newDecision.getReasoning() != null ? newDecision.getReasoning() : "N/A", "inline", false)
                ),
                "timestamp", Instant.now().toString()
        );

        sendWebhook(webhookUrl, Map.of("embeds", List.of(embed)));
    }

    private String getWebhookUrl(Integer raceWeekendId) {
        RaceWeekend weekend = weekendMapper.findById(raceWeekendId);
        if (weekend == null || weekend.getDiscordWebhookUrl() == null || weekend.getDiscordWebhookUrl().isBlank()) {
            return null;
        }
        return weekend.getDiscordWebhookUrl();
    }

    private void sendWebhook(String webhookUrl, Map<String, Object> payload) {
        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to send Discord webhook notification", e);
        }
    }
}
