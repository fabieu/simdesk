package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
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
            @Result(id = true, property = "playerId", column = "player_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "shortName", column = "short_name"),
            @Result(property = "locked", column = "locked"),
            @Result(property = "driveTimeMillis", column = "drive_time_millis"),
            @Result(property = "validLapsCount", column = "valid_lap_count"),
            @Result(property = "invalidLapsCount", column = "invalid_lap_count"),
            @Result(property = "lastActivity", column = "last_activity")
    })
    @Select("SELECT * FROM simdesk.driver")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM driver")
    List<Driver> findAll();

    @Select("SELECT COUNT(player_id) FROM simdesk.driver")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT COUNT(player_id) FROM driver")
    @ResultType(long.class)
    long count();

    @ResultMap("driverResultMap")
    @Select("SELECT * FROM simdesk.driver WHERE player_id = #{playerId}")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM driver WHERE player_id = #{playerId}")
    Driver findByPlayerId(String playerId);

    @SuppressWarnings("unused")
    @ResultMap("driverResultMap")
    @Select("""
            SELECT driver.*,
                   leaderboard_driver.drive_time_millis,
                   COUNT(CASE WHEN lap.valid THEN 1 END)     AS valid_lap_count,
                   COUNT(CASE WHEN NOT lap.valid THEN 1 END) AS invalid_lap_count
            FROM simdesk.driver
                     INNER JOIN simdesk.leaderboard_driver ON driver.player_id = leaderboard_driver.player_id
                     INNER JOIN simdesk.lap ON (leaderboard_driver.session_id = lap.session_id AND leaderboard_driver.player_id = lap.driver_id)
            WHERE leaderboard_driver.car_id = #{carId}
              AND leaderboard_driver.session_id = #{sessionId}
            GROUP BY driver.player_id, leaderboard_driver.drive_time_millis;
            """)
    @Select(databaseId = DatabaseVendor.SQLITE, value = """
            SELECT driver.*,
                   leaderboard_driver.drive_time_millis,
                   COUNT(CASE WHEN lap.valid THEN 1 END)     AS valid_lap_count,
                   COUNT(CASE WHEN NOT lap.valid THEN 1 END) AS invalid_lap_count
            FROM driver
                     INNER JOIN leaderboard_driver ON driver.player_id = leaderboard_driver.player_id
                     INNER JOIN lap ON (leaderboard_driver.session_id = lap.session_id AND leaderboard_driver.player_id = lap.driver_id)
            WHERE leaderboard_driver.car_id = #{carId}
              AND leaderboard_driver.session_id = #{sessionId}
            GROUP BY driver.player_id, leaderboard_driver.drive_time_millis;
            """)
    List<Driver> findDriversBySessionAndCarId(Integer sessionId, Integer carId);

    @Insert("""
            INSERT INTO simdesk.driver AS d (player_id, first_name, last_name, short_name, last_activity)
            VALUES (#{playerId}, #{firstName}, #{lastName}, #{shortName}, #{lastActivity})
            ON CONFLICT(player_id) DO UPDATE SET first_name = excluded.first_name, last_name = excluded.last_name, short_name = excluded.short_name, last_activity = (SELECT CASE WHEN d.last_activity IS NULL OR d.last_activity < excluded.last_activity THEN excluded.last_activity ELSE d.last_activity END)
            """)
    @Insert(databaseId = DatabaseVendor.SQLITE, value = """
            INSERT INTO driver AS d (player_id, first_name, last_name, short_name, last_activity)
            VALUES (#{playerId}, #{firstName}, #{lastName}, #{shortName}, #{lastActivity})
            ON CONFLICT(player_id) DO UPDATE SET first_name = excluded.first_name, last_name = excluded.last_name, short_name = excluded.short_name, last_activity = (SELECT CASE WHEN d.last_activity IS NULL OR d.last_activity < excluded.last_activity THEN excluded.last_activity ELSE d.last_activity END)
            """)
    void upsert(Driver driver);
}
