package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.comparator.DriverRankingComparator;
import de.sustineo.simdesk.entities.comparator.GroupRankingComparator;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import de.sustineo.simdesk.mapper.RankingMapper;
import de.sustineo.simdesk.views.enums.TimeRange;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class RankingService {
    private final RankingMapper rankingMapper;

    public RankingService(RankingMapper rankingMapper) {
        this.rankingMapper = rankingMapper;
    }

    public List<GroupRanking> getAllTimeGroupRanking(TimeRange timeRange) {
        List<GroupRanking> groupRankings = rankingMapper.findAllTimeFastestLaps(timeRange.from(), timeRange.to());
        return getRankingsByCarGroupAndTrackId(groupRankings);
    }

    public List<DriverRanking> getAllTimeDriverRanking(CarGroup carGroup, String trackId, TimeRange timeRange) {
        List<DriverRanking> driverRankings = rankingMapper.findAllTimeFastestLapsByTrack(carGroup, trackId, timeRange.from(), timeRange.to());
        driverRankings = getRankingByDriverIdAndCarModel(driverRankings);
        addRanking(driverRankings);

        return driverRankings;
    }

    private void addRanking(List<DriverRanking> driverRankings) {
        for (DriverRanking driverRanking : driverRankings) {
            driverRanking.setRanking(driverRankings.indexOf(driverRanking) + 1);
        }
    }

    private List<GroupRanking> getRankingsByCarGroupAndTrackId(List<GroupRanking> groupRankings) {
        HashMap<Pair<CarGroup, String>, GroupRanking> fastestLapsByCarGroupAndTrackId = new HashMap<>();

        for (GroupRanking groupRanking : groupRankings) {
            Pair<CarGroup, String> key = Pair.of(groupRanking.getCarGroup(), groupRanking.getTrackId());
            GroupRanking value = fastestLapsByCarGroupAndTrackId.get(key);

            if (value == null || value.getLapTimeMillis() >= groupRanking.getLapTimeMillis()) {
                fastestLapsByCarGroupAndTrackId.put(key, groupRanking);
            }
        }

        return fastestLapsByCarGroupAndTrackId.values().stream()
                .sorted(new GroupRankingComparator())
                .toList();
    }

    private List<DriverRanking> getRankingByDriverIdAndCarModel(List<DriverRanking> driverRankings) {
        HashMap<Pair<String, Integer>, DriverRanking> fastestLapsByDriverAndCarModel = new HashMap<>();

        for (DriverRanking driverRanking : driverRankings) {
            Pair<String, Integer> key = Pair.of(driverRanking.getDriver().getId(), driverRanking.getCarModelId());
            DriverRanking value = fastestLapsByDriverAndCarModel.get(key);

            if (value == null || value.getLapTimeMillis() >= driverRanking.getLapTimeMillis()) {
                fastestLapsByDriverAndCarModel.put(key, driverRanking);
            }
        }

        return fastestLapsByDriverAndCarModel.values().stream()
                .sorted(new DriverRankingComparator())
                .toList();
    }
}
