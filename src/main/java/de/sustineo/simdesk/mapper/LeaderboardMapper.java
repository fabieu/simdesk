package de.sustineo.simdesk.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.LeaderboardLine;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Component
@Mapper
public interface LeaderboardMapper {
    @Results(id = "leaderboardLineResultMap", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "ranking", column = "ranking"),
            @Result(property = "carId", column = "car_id"),
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "ballastKg", column = "ballast_kg"),
            @Result(property = "raceNumber", column = "race_number"),
            @Result(property = "drivers", column = "{sessionId=session_id, carId=car_id}", many = @Many(select = "de.sustineo.simdesk.mapper.DriverMapper.findBySessionIdAndCarId")),
            @Result(property = "bestLapTimeMillis", column = "best_lap_time_millis"),
            @Result(property = "bestSplit1Millis", column = "best_split1_millis"),
            @Result(property = "bestSplit2Millis", column = "best_split2_millis"),
            @Result(property = "bestSplit3Millis", column = "best_split3_millis"),
            @Result(property = "totalTimeMillis", column = "total_time_millis"),
            @Result(property = "lapCount", column = "lap_count")
    })
    @Select("SELECT * FROM leaderboard_line WHERE session_id = #{sessionId} ORDER BY ranking")
    List<LeaderboardLine> findBySessionIdOrderByRanking(Integer sessionId);

    @Insert("""
            INSERT INTO leaderboard_line (session_id, ranking, cup_category, car_id, car_model_id, ballast_kg, race_number, best_lap_time_millis, best_split1_millis, best_split2_millis, best_split3_millis, total_time_millis, lap_count)
            VALUES (#{session.id}, #{ranking}, #{cupCategory}, #{carId}, #{carModelId}, #{ballastKg}, #{raceNumber}, #{bestLapTimeMillis}, #{bestSplit1Millis}, #{bestSplit2Millis}, #{bestSplit3Millis}, #{totalTimeMillis}, #{lapCount})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertLeaderboardLine(LeaderboardLine leaderboardLines);

    @Insert("""
            INSERT INTO leaderboard_driver (session_id, car_id, driver_id, drive_time_millis)
            VALUES (#{sessionId}, #{carId}, #{driverId}, #{driveTimeMillis})
            """)
    void insertLeaderboardDriver(Integer sessionId, Integer carId, String driverId, Long driveTimeMillis);
}
