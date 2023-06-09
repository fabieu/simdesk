package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.DriverRanking;
import de.sustineo.acc.leaderboard.entities.GroupRanking;
import de.sustineo.acc.leaderboard.entities.comparator.DriverRankingComparator;
import de.sustineo.acc.leaderboard.entities.comparator.GroupRankingComparator;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.entities.mapper.RankingMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RankingService {
    private final RankingMapper rankingMapper;

    public RankingService(RankingMapper rankingMapper) {
        this.rankingMapper = rankingMapper;
    }

    public List<GroupRanking> getAllTimeGroupRanking() {
        List<GroupRanking> groupRankings = rankingMapper.findAllTimeFastestLaps();
        groupRankings = getRankingsGroupedByCarGroupAndTrackId(groupRankings);

        return sortGroupRankings(groupRankings);
    }

    public List<DriverRanking> getAllTimeDriverRanking(String carGroup, String trackId) {
        List<DriverRanking> driverRankings = rankingMapper.findAllTimeFastestLapsByTrack(carGroup, trackId);
        driverRankings = getRankingGroupedByDriverId(driverRankings);

        return sortDriverRankings(driverRankings);
    }

    private List<GroupRanking> sortGroupRankings(List<GroupRanking> groupRankings) {
        return groupRankings.stream()
                .sorted(new GroupRankingComparator())
                .toList();
    }

    private List<DriverRanking> sortDriverRankings(List<DriverRanking> driverRankings) {
        return driverRankings.stream()
                .sorted(new DriverRankingComparator())
                .toList();
    }

    private List<GroupRanking> getRankingsGroupedByCarGroupAndTrackId(List<GroupRanking> groupRankings) {
        Map<Pair<CarGroup, String>, GroupRanking> fastestLaps = groupRankings.stream()
                .collect(Collectors.groupingBy(
                        r -> Pair.of(r.getCarGroup(), r.getTrackId()),
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(GroupRanking::getLapTimeMillis)),
                                Optional::get
                        ))
                );

        return fastestLaps.values().stream().toList();
    }

    private List<DriverRanking> getRankingGroupedByDriverId(List<DriverRanking> driverRankings) {
        Map<String, DriverRanking> fastestLaps = driverRankings.stream()
                .collect(Collectors.groupingBy(
                        driverRanking -> driverRanking.getDriver().getPlayerId(),
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(DriverRanking::getLapTimeMillis)),
                                Optional::get
                        ))
                );

        return fastestLaps.values().stream().toList();
    }
}
