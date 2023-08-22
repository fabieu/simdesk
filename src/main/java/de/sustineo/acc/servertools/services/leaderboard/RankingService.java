package de.sustineo.acc.servertools.services.leaderboard;

import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.Session;
import de.sustineo.acc.servertools.entities.comparator.DriverRankingComparator;
import de.sustineo.acc.servertools.entities.comparator.GroupRankingComparator;
import de.sustineo.acc.servertools.entities.enums.CarGroup;
import de.sustineo.acc.servertools.entities.mapper.RankingMapper;
import de.sustineo.acc.servertools.entities.ranking.DriverRanking;
import de.sustineo.acc.servertools.entities.ranking.GroupRanking;
import de.sustineo.acc.servertools.entities.ranking.SessionRanking;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class RankingService {
    private final SessionService sessionService;
    private final RankingMapper rankingMapper;

    public RankingService(SessionService sessionService, RankingMapper rankingMapper) {
        this.sessionService = sessionService;
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

    public List<SessionRanking> getSessionRanking(String fileChecksum) {
        Session session = sessionService.getSession(fileChecksum);
        if (session == null) {
            return null;
        }

        return rankingMapper.findLeaderboardLinesBySessionId(session.getId());
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
