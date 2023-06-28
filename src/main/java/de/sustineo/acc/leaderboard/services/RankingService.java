package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.comparator.DriverRankingComparator;
import de.sustineo.acc.leaderboard.entities.comparator.GroupRankingComparator;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.entities.mapper.RankingMapper;
import de.sustineo.acc.leaderboard.entities.ranking.DriverRanking;
import de.sustineo.acc.leaderboard.entities.ranking.GroupRanking;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RankingService {
    private final RankingMapper rankingMapper;

    public RankingService(RankingMapper rankingMapper) {
        this.rankingMapper = rankingMapper;
    }

    public List<GroupRanking> getAllTimeGroupRanking() {
        List<GroupRanking> groupRankings = rankingMapper.findAllTimeFastestLaps();
        return getRankingsByCarGroupAndTrackId(groupRankings);
    }

    public List<DriverRanking> getAllTimeDriverRanking(String carGroup, String trackId) {
        List<DriverRanking> driverRankings = rankingMapper.findAllTimeFastestLapsByTrack(carGroup, trackId);
        driverRankings = getRankingByPlayerIdAndCarModel(driverRankings);
        addRanking(driverRankings);

        return driverRankings;
    }

    public List<SessionRanking> getSessionRanking(Integer sessionId) {
        return rankingMapper.findLeaderboardLinesBySessionId(sessionId);
    }

    private void addRanking(List<DriverRanking> driverRankings) {
        for (DriverRanking driverRanking : driverRankings){
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

    private List<DriverRanking> getRankingByPlayerIdAndCarModel(List<DriverRanking> driverRankings) {
        HashMap<Pair<String, Integer>, DriverRanking> fastestLapsByDriverAndCarModel = new HashMap<>();

        for (DriverRanking driverRanking : driverRankings) {
            Pair<String, Integer> key = Pair.of(driverRanking.getDriver().getPlayerId(), driverRanking.getCarModelId());
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
