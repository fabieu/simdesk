package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.LeaderboardLine;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface LeaderboardMapper {
    @Insert("INSERT INTO acc_leaderboard.leaderboard_lines (session_id, ranking, cup_category, car_id, car_group, car_model_id, race_number, best_lap_time_millis, best_split1_millis, best_split2_millis, best_split3_millis, total_time_millis, lap_count) " +
            "VALUES (#{sessionId}, #{ranking}, #{cupCategory}, #{carId}, #{carGroup}, #{carModelId}, #{raceNumber}, #{bestLapTimeMillis}, #{bestSplit1Millis}, #{bestSplit2Millis}, #{bestSplit3Millis}, #{totalTimeMillis}, #{lapCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertLeaderboardLine(LeaderboardLine leaderboardLines);

    @Insert("INSERT INTO acc_leaderboard.leaderboard_drivers (session_id, car_id, player_id, drive_time_millis) " +
            "VALUES (#{sessionId}, #{carId}, #{playerId}, #{driveTimeMillis})")
    void insertLeaderboardDriver(Integer sessionId, Integer carId, String playerId, Long driveTimeMillis);
}
