package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.LeaderboardDriver;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.json.kunos.acc.AccLeaderboardLine;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.repositories.LeaderboardDriverRepository;
import de.sustineo.simdesk.repositories.LeaderboardLineRepository;
import de.sustineo.simdesk.services.converter.LeaderboardConverter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class LeaderboardService {
    private final LeaderboardConverter leaderboardConverter;
    private final LeaderboardLineRepository leaderboardLineRepository;
    private final LeaderboardDriverRepository leaderboardDriverRepository;
    private final DriverService driverService;

    public LeaderboardService(LeaderboardConverter leaderboardConverter,
                              LeaderboardLineRepository leaderboardLineRepository,
                              LeaderboardDriverRepository leaderboardDriverRepository,
                              DriverService driverService) {
        this.leaderboardConverter = leaderboardConverter;
        this.leaderboardLineRepository = leaderboardLineRepository;
        this.leaderboardDriverRepository = leaderboardDriverRepository;
        this.driverService = driverService;
    }

    @Transactional
    public void processLeaderboardLines(Long sessionId, AccSession accSession, FileMetadata fileMetadata) {
        List<AccLeaderboardLine> accLeaderboardLines = accSession.getSessionResult().getLeaderboardLines();

        for (int i = 0; i < accLeaderboardLines.size(); i++) {
            AccLeaderboardLine accLeaderboardLine = accLeaderboardLines.get(i);

            LeaderboardLine leaderboardLine = leaderboardConverter.convertToLeaderboardLine(i, sessionId, accLeaderboardLine, fileMetadata);
            leaderboardLineRepository.save(leaderboardLine);

            List<LeaderboardDriver> leaderboardDrivers = leaderboardConverter.convertToLeaderboardDrivers(sessionId, accLeaderboardLine, fileMetadata);
            leaderboardDrivers.forEach(leaderboardDriver -> driverService.upsertDriver(leaderboardDriver.getDriver()));
            leaderboardDriverRepository.saveAll(leaderboardDrivers);
        }
    }
}
