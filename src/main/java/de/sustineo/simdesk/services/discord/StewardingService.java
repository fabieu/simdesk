package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.entities.discord.Command;
import de.sustineo.simdesk.entities.discord.Modal;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import discord4j.core.spec.InteractionPresentModalSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StewardingService {
    private static final String COMMAND_STEWARD_DECISION = "steward-decision";
    private static final String MODAL_STEWARD_DECISION = "steward-decision";
    static final String PARAGRAPHINPUT_CUSTOM_ID = "my-paragraph-input";
    static final String INPUT_CUSTOM_ID = "my-input";

    public void registerCommands(List<ApplicationCommandRequest> applicationCommandRequests, Map<String, Command> commands, Map<String, Modal> modals) {
        ApplicationCommandRequest applicationCommand = ApplicationCommandRequest.builder()
                .name(COMMAND_STEWARD_DECISION)
                .description("Create a unified embed for publishing steward decisions")
                .build();
        applicationCommandRequests.add(applicationCommand);

        commands.put(COMMAND_STEWARD_DECISION, event -> {
            InteractionPresentModalSpec modalSpec = InteractionPresentModalSpec.builder()
                    .title("Example modal")
                    .customId(MODAL_STEWARD_DECISION)
                    .addAllComponents(Arrays.asList(
                            ActionRow.of(TextInput.small(INPUT_CUSTOM_ID, "A title?").required(false)),
                            ActionRow.of(TextInput.paragraph(PARAGRAPHINPUT_CUSTOM_ID, "Tell us something...", 250, 928).placeholder("...in more than 250 characters but less than 928").required(true))
                    ))
                    .build();
            event.presentModal(modalSpec).subscribe();
        });

        modals.put(MODAL_STEWARD_DECISION, event -> {
            String story = "";
            String comments = "";

            for (TextInput component : event.getComponents(TextInput.class)) {
                if (PARAGRAPHINPUT_CUSTOM_ID.equals(component.getCustomId())) {
                    story = component.getValue().orElse("untiteled");
                } else if (INPUT_CUSTOM_ID.equals(component.getCustomId())) {
                    comments = component.getValue().orElse("");
                }
            }

            event.reply("You wrote: " + story + "\n\nComments: " + comments)
                    .subscribe();
        });
    }
}
