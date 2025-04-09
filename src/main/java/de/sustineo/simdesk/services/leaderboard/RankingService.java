package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.comparator.GroupRankingComparator;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import de.sustineo.simdesk.mapper.RankingMapper;
import de.sustineo.simdesk.views.enums.TimeRange;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class RankingService {
    private final RankingMapper rankingMapper;

    public RankingService(RankingMapper rankingMapper) {
        this.rankingMapper = rankingMapper;
    }

    public List<GroupRanking> getAllTimeGroupRanking(TimeRange timeRange) {
        return rankingMapper.findAllTimeFastestLaps(timeRange.from(), timeRange.to()).stream()
                .sorted(new GroupRankingComparator())
                .toList();
    }

    public List<DriverRanking> getAllTimeDriverRanking(CarGroup carGroup, String trackId, TimeRange timeRange) {
        List<DriverRanking> driverRankings = rankingMapper.findAllTimeFastestLapsByTrack(carGroup, trackId, timeRange.from(), timeRange.to());

        int ranking = 1;
        for (DriverRanking driverRanking : driverRankings) {
            driverRanking.setRanking(ranking);
            ranking++;
        }

        return driverRankings;
    }
}
