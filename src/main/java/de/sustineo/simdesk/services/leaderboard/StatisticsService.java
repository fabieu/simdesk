package de.sustineo.simdesk.services.leaderboard;


import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Stats;
import de.sustineo.simdesk.utils.FormatUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class StatisticsService {
    private final SessionService sessionService;
    private final DriverService driverService;
    private final LapService lapService;

    public StatisticsService(SessionService sessionService, DriverService driverService, LapService lapService) {
        this.sessionService = sessionService;
        this.driverService = driverService;
        this.lapService = lapService;
    }

    public Stats getLeaderboardStatistics() {
        return Stats.builder()
                .totalSessions(FormatUtils.formatLargeNumber(sessionService.getSessionCount()))
                .totalDrivers(FormatUtils.formatLargeNumber(driverService.getDriverCount()))
                .totalLaps(FormatUtils.formatLargeNumber(lapService.getLapCount()))
                .build();
    }
}
