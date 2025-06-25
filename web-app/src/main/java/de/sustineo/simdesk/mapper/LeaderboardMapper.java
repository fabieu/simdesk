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
            @Result(property = "bestSector1Millis", column = "best_sector1_millis"),
            @Result(property = "bestSector2Millis", column = "best_sector2_millis"),
            @Result(property = "bestSector3Millis", column = "best_sector3_millis"),
            @Result(property = "totalTimeMillis", column = "total_time_millis"),
            @Result(property = "lapCount", column = "lap_count")
    })
    @Select("SELECT * FROM leaderboard_line WHERE session_id = #{sessionId} ORDER BY ranking")
    List<LeaderboardLine> findBySessionIdOrderByRanking(Integer sessionId);

    @Insert("""
            INSERT INTO leaderboard_line (session_id, ranking, cup_category, car_id, car_model_id, ballast_kg, race_number, best_lap_time_millis, best_sector1_millis, best_sector2_millis, best_sector3_millis, total_time_millis, lap_count)
            VALUES (#{session.id}, #{ranking}, #{cupCategory}, #{carId}, #{carModelId}, #{ballastKg}, #{raceNumber}, #{bestLapTimeMillis}, #{bestSector1Millis}, #{bestSector2Millis}, #{bestSector3Millis}, #{totalTimeMillis}, #{lapCount})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertLeaderboardLine(LeaderboardLine leaderboardLines);

    @Insert("""
            INSERT INTO leaderboard_driver (session_id, car_id, driver_id, drive_time_millis)
            VALUES (#{sessionId}, #{carId}, #{driverId}, #{driveTimeMillis})
            """)
    void insertLeaderboardDriver(Integer sessionId, Integer carId, String driverId, Long driveTimeMillis);
}
