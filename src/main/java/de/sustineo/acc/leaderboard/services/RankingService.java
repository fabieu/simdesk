package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.entities.Ranking;
import de.sustineo.acc.leaderboard.entities.comparator.RankingComparator;
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
    private final DriverService driverService;

    public RankingService(RankingMapper rankingMapper, DriverService driverService) {
        this.rankingMapper = rankingMapper;
        this.driverService = driverService;
    }

    public List<Ranking> getGlobalRanking() {
        List<Ranking> rankings = rankingMapper.findGlobalFastestLaps();
        rankings = getRankingsGroupedByCarGroupAndTrackId(rankings);

        return sortRankings(rankings);
    }

    private List<Ranking> sortRankings(List<Ranking> rankings) {
        return rankings.stream()
                .sorted(new RankingComparator())
                .toList();
    }

    private List<Ranking> getRankingsGroupedByCarGroupAndTrackId(List<Ranking> rankings) {
        Map<Pair<CarGroup, String>, Ranking> fastestLaps = rankings.stream()
                .collect(Collectors.groupingBy(
                        r -> Pair.of(r.getCarGroup(), r.getTrackId()),
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(Ranking::getLapTimeMillis)),
                                Optional::get
                        ))
                );

        return fastestLaps.values().stream().toList();
    }

    private Driver getDriver(String playerId) {
        return driverService.findByPlayerId(playerId);
    }
}
