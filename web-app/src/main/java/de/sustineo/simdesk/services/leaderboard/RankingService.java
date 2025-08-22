package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.comparator.DriverRankingComparator;
import de.sustineo.simdesk.entities.comparator.GroupRankingComparator;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import de.sustineo.simdesk.mybatis.mapper.RankingMapper;
import de.sustineo.simdesk.views.enums.TimeRange;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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

    public List<DriverRanking> getAllTimeDriverRanking(@Nonnull CarGroup carGroup, @Nonnull String trackId, @Nonnull TimeRange timeRange, @Nullable AccCar car) {
        List<DriverRanking> driverRankings = rankingMapper.findAllTimeFastestLapsByTrack(carGroup, trackId, timeRange.from(), timeRange.to(), car).stream()
                .sorted(new DriverRankingComparator())
                .toList();

        int ranking = 1;
        for (DriverRanking driverRanking : driverRankings) {
            driverRanking.setRanking(ranking);
            ranking++;
        }

        return driverRankings;
    }
}
