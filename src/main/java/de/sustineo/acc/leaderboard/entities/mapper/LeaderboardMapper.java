package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.LeaderboardLine;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface LeaderboardMapper {
    @Results(id = "leaderboardResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "ranking", column = "ranking"),
            @Result(property = "cupCategory", column = "cup_category"),
            @Result(property = "carId", column = "car_id"),
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "raceNumber", column = "race_number"),
            @Result(property = "drivers", column = "{sessionId=session_id, carId=car_id}", many = @Many(select = "de.sustineo.acc.leaderboard.entities.mapper.DriverMapper.findDriversBySessionAndCarId")),
            @Result(property = "bestLapTimeMillis", column = "best_lap_time_millis"),
            @Result(property = "bestSplit1Millis", column = "best_split1_millis"),
            @Result(property = "bestSplit2Millis", column = "best_split2_millis"),
            @Result(property = "bestSplit3Millis", column = "best_split3_millis"),
            @Result(property = "totalTimeMillis", column = "total_time_millis"),
            @Result(property = "lapCount", column = "lap_count")
    })
    @Select("SELECT * FROM acc_leaderboard.leaderboard_lines WHERE session_id = #{sessionId}")
    List<LeaderboardLine> findBySessionId(Integer sessionId);

    @Insert("INSERT INTO acc_leaderboard.leaderboard_lines (session_id, ranking, cup_category, car_id, car_group, car_model_id, race_number, best_lap_time_millis, best_split1_millis, best_split2_millis, best_split3_millis, total_time_millis, lap_count) " +
            "VALUES (#{sessionId}, #{ranking}, #{cupCategory}, #{carId}, #{carGroup}, #{carModelId}, #{raceNumber}, #{bestLapTimeMillis}, #{bestSplit1Millis}, #{bestSplit2Millis}, #{bestSplit3Millis}, #{totalTimeMillis}, #{lapCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertLeaderboardLine(LeaderboardLine leaderboardLines);

    @Insert("INSERT INTO acc_leaderboard.leaderboard_drivers (session_id, car_id, player_id, drive_time_millis) " +
            "VALUES (#{sessionId}, #{carId}, #{playerId}, #{driveTimeMillis})")
    void insertLeaderboardDriver(Integer sessionId, Integer carId, String playerId, Long driveTimeMillis);
}
