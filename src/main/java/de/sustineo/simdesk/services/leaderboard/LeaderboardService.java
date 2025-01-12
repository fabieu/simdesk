package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.LeaderboardDriver;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.Session;
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
    public void processLeaderboardLines(Session session, AccSession accSession, FileMetadata fileMetadata) {
        List<AccLeaderboardLine> accLeaderboardLines = accSession.getSessionResult().getLeaderboardLines();

        for (int i = 0; i < accLeaderboardLines.size(); i++) {
            AccLeaderboardLine accLeaderboardLine = accLeaderboardLines.get(i);

            LeaderboardLine leaderboardLine = leaderboardConverter.convertToLeaderboardLine(i, session, accLeaderboardLine);
            leaderboardLineRepository.save(leaderboardLine);

            List<LeaderboardDriver> leaderboardDrivers = leaderboardConverter.convertToLeaderboardDrivers(session, accLeaderboardLine, fileMetadata);
            leaderboardDrivers.forEach(leaderboardDriver -> driverService.upsertDriver(leaderboardDriver.getDriver()));
            leaderboardDriverRepository.saveAll(leaderboardDrivers);
        }
    }

    @Transactional
    public List<String> getPlayerIdsBySessionAndCarId(Long sessionId, Integer carId) {
        List<LeaderboardDriver> leaderboardDrivers = leaderboardDriverRepository.findBySessionIdAndCarId(sessionId, carId);

        return leaderboardDrivers.stream()
                .map(leaderboardDriver -> leaderboardDriver.getDriver().getPlayerId())
                .toList();
    }

    @Transactional
    public List<LeaderboardLine> getLeaderboardLinesBySessionId(Session session) {
        if (session == null) {
            return null;
        }

        return leaderboardLineRepository.findBySessionIdOrderByRanking(session.getId());
    }
}
