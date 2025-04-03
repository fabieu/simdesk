package de.sustineo.simdesk.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Component
@Mapper
@SuppressWarnings("unused")
public interface DriverMapper {
    @Results(id = "driverResultMap", value = {
            @Result(id = true, property = "id", column = "driver_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "shortName", column = "short_name"),
            @Result(property = "visibility", column = "visibility"),
            @Result(property = "driveTimeMillis", column = "drive_time_millis"),
            @Result(property = "validLapsCount", column = "valid_lap_count"),
            @Result(property = "invalidLapsCount", column = "invalid_lap_count"),
            @Result(property = "lastActivity", column = "last_activity")
    })
    @Select("SELECT * FROM driver ORDER BY last_activity DESC")
    List<Driver> findAll();


    @ResultMap("driverResultMap")
    @Select("SELECT * FROM driver WHERE driver_id = #{driverId}")
    Driver findById(String driverId);

    @SuppressWarnings("unused")
    @ResultMap("driverResultMap")
    @Select("""
            SELECT driver.*,
                   leaderboard_driver.drive_time_millis,
                   COUNT(CASE WHEN lap.valid THEN 1 END)     AS valid_lap_count,
                   COUNT(CASE WHEN NOT lap.valid THEN 1 END) AS invalid_lap_count
            FROM driver
                     INNER JOIN leaderboard_driver ON driver.driver_id = leaderboard_driver.driver_id
                     INNER JOIN lap ON (leaderboard_driver.session_id = lap.session_id AND leaderboard_driver.driver_id = lap.driver_id)
            WHERE leaderboard_driver.car_id = #{carId}
              AND leaderboard_driver.session_id = #{sessionId}
            GROUP BY driver.driver_id, leaderboard_driver.drive_time_millis;
            """)
    List<Driver> findBySessionIdAndCarId(Integer sessionId, Integer carId);

    @ResultType(List.class)
    @Select("SELECT driver_id FROM leaderboard_driver WHERE car_id = #{carId} AND session_id = #{sessionId}")
    List<String> findDriverIdsBySessionIdAndCarId(Integer sessionId, Integer carId);


    @Insert("""
            INSERT INTO driver AS d (driver_id, first_name, last_name, short_name, last_activity, visibility)
            VALUES (#{id}, #{firstName}, #{lastName}, #{shortName}, #{lastActivity}, COALESCE(#{visibility}, 'PUBLIC'))
            ON CONFLICT(driver_id) DO UPDATE SET
              first_name = COALESCE(excluded.first_name, d.first_name),
              last_name = COALESCE(excluded.last_name, d.last_name),
              short_name = COALESCE(excluded.short_name, d.short_name),
              visibility = COALESCE(excluded.visibility, d.visibility, 'PUBLIC'),
              last_activity = (SELECT CASE WHEN d.last_activity IS NULL OR d.last_activity < excluded.last_activity THEN excluded.last_activity ELSE d.last_activity END)
            """)
    void upsert(Driver driver);

    @Update("""
            UPDATE driver
            SET visibility = COALESCE(#{visibility}, visibility)
            WHERE driver_id = #{id}
            """)
    void updateVisibility(Driver driver);
}
