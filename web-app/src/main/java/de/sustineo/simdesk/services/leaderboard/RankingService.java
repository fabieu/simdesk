package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.comparator.DriverRankingComparator;
import de.sustineo.simdesk.entities.comparator.GroupRankingComparator;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.DriverBestSectors;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import de.sustineo.simdesk.mybatis.mapper.RankingMapper;
import de.sustineo.simdesk.views.enums.TimeRange;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingMapper rankingMapper;
    private final Executor taskExecutor;

    public List<GroupRanking> getAllTimeGroupRanking(TimeRange timeRange) {
        return rankingMapper.findAllTimeFastestLaps(timeRange.from(), timeRange.to()).stream()
                .sorted(new GroupRankingComparator())
                .toList();
    }

    public List<DriverRanking> getAllTimeDriverRanking(@Nonnull CarGroup carGroup, @Nonnull String trackId, @Nonnull TimeRange timeRange, @Nullable AccCar car) {
        AtomicInteger ranking = new AtomicInteger(1);

        CompletableFuture<Map<String, DriverBestSectors>> bestSectorsFuture = CompletableFuture.supplyAsync(() ->
                rankingMapper.findBestSectorsByTrack(carGroup, trackId, timeRange.from(), timeRange.to(), car)
                        .stream()
                        .collect(Collectors.toMap(DriverBestSectors::getDriverId, Function.identity())), taskExecutor);

        CompletableFuture<List<DriverRanking>> rankingsFuture = CompletableFuture.supplyAsync(() ->
                rankingMapper.findAllTimeFastestLapsByTrack(carGroup, trackId, timeRange.from(), timeRange.to(), car), taskExecutor);

        return rankingsFuture.thenCombine(bestSectorsFuture, (rankings, bestSectors) ->
                        rankings.stream()
                                .sorted(new DriverRankingComparator())
                                .peek(driverRanking -> {
                                    driverRanking.setRanking(ranking.getAndIncrement());
                                    driverRanking.setBestSectors(bestSectors.get(driverRanking.getDriver().getId()));
                                })
                                .toList())
                .join(); // Propagates unchecked exceptions if any
    }
}
