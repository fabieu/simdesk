package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.mapper.StewardingMapper;
import de.sustineo.simdesk.entities.stewarding.StewardingEvent;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Profile(ProfileManager.PROFILE_STEWARDING)
@Service
public class StewardingService {
    private final StewardingMapper stewardingMapper;

    public StewardingService(StewardingMapper stewardingMapper) {
        this.stewardingMapper = stewardingMapper;
    }

    public List<StewardingEvent> getAllActiveEvents() {
        return stewardingMapper.findAllActiveEvents();
    }

    public void archiveEvent(StewardingEvent stewardingEvent) {
        stewardingMapper.archiveEvent(stewardingEvent);
    }

    public void saveEvent(StewardingEvent stewardingEvent) {

    }
}
