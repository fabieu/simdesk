package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.repositories.LapRepository;
import de.sustineo.simdesk.services.converter.LapConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Log
@Service
public class LapService {
    private final DriverService driverService;
    private final LapConverter lapConverter;
    private final LapRepository lapRepository;

    @Autowired
    public LapService(DriverService driverService,
                      LapConverter lapConverter,
                      LapRepository lapRepository) {
        this.driverService = driverService;
        this.lapConverter = lapConverter;
        this.lapRepository = lapRepository;
    }

    public void handleLaps(Long sessionId, AccSession accSession, FileMetadata fileMetadata) {
        List<Lap> laps = lapConverter.convertToLaps(sessionId, accSession, fileMetadata);
        for (Lap lap : laps) {
            driverService.upsertDriver(lap.getDriver());
            lapRepository.save(lap);
        }
    }

    public List<Lap> getLapsBySessionAndDrivers(Long sessionId, List<String> playerIds) {
        return lapRepository.findBySessionAndPlayerIdsOrderByIdAsc(sessionId, playerIds);
    }
}
