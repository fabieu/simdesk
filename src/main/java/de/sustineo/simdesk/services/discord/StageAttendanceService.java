package de.sustineo.simdesk.services.discord;

import de.sustineo.simdesk.configuration.ProfileManager;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class StageAttendanceService {
    public void handleStageEvent(MessageCreateEvent event) {
        log.info("Handling stage event: " + event);
    }
}
