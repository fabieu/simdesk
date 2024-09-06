package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.entities.discord.Command;
import de.sustineo.simdesk.entities.discord.Modal;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionPresentModalSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StewardingService {
    private static final String COMMAND_STEWARD_DECISION = "steward-decision";
    private static final String MODAL_CUSTOM_ID = "steward-decision";
    private static final String TITLE_CUSTOM_ID = "title-custom-id";
    private static final String REPORT_CUSTOM_ID = "report-custom-id";
    private static final String PENALTY_CUSTOM_ID = "penalty-custom-id";
    private static final String REASON_CUSTOM_ID = "reason-custom-id";


    public void registerCommands(List<ApplicationCommandRequest> applicationCommandRequests, Map<String, Command> commands, Map<String, Modal> modals) {
        ApplicationCommandRequest applicationCommand = ApplicationCommandRequest.builder()
                .name(COMMAND_STEWARD_DECISION)
                .description("Create a unified embed for publishing steward decisions")
                .build();
        applicationCommandRequests.add(applicationCommand);

        commands.put(COMMAND_STEWARD_DECISION, event -> {
            InteractionPresentModalSpec modalSpec = InteractionPresentModalSpec.builder()
                    .title("Steward Decision")
                    .customId(MODAL_CUSTOM_ID)
                    .addAllComponents(Arrays.asList(
                            ActionRow.of(TextInput.small(TITLE_CUSTOM_ID, "Title").required(true).prefilled("Incident")),
                            ActionRow.of(TextInput.paragraph(REPORT_CUSTOM_ID, "Report").required(true)),
                            ActionRow.of(TextInput.small(PENALTY_CUSTOM_ID, "Penalty").required(true)),
                            ActionRow.of(TextInput.paragraph(REASON_CUSTOM_ID, "Reason").required(true))
                    ))
                    .build();
            event.presentModal(modalSpec).subscribe();
        });

        modals.put(MODAL_CUSTOM_ID, event -> {
            String title = "";
            String report = "";
            String penalty = "";
            String reason = "";

            for (TextInput component : event.getComponents(TextInput.class)) {
                if (TITLE_CUSTOM_ID.equals(component.getCustomId())) {
                    title = component.getValue().orElse("");
                } else if (REPORT_CUSTOM_ID.equals(component.getCustomId())) {
                    report = component.getValue().orElse("");
                } else if (PENALTY_CUSTOM_ID.equals(component.getCustomId())) {
                    penalty = component.getValue().orElse("");
                } else if (REASON_CUSTOM_ID.equals(component.getCustomId())) {
                    reason = component.getValue().orElse("");
                }
            }

            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                    .title(title)
                    .addField("Report", report, false)
                    .addField("Penalty", penalty, false)
                    .addField("Reason", reason, false)
                    .color(Color.of(139, 0, 0)) // Darkred
                    .timestamp(Instant.now())
                    .build();

            event.reply()
                    .withEmbeds(embed)
                    .subscribe();
        });
    }
}
