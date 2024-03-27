package de.sustineo.simdesk.entities.mapper;

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
            @Result(id = true, property = "playerId", column = "player_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "shortName", column = "short_name"),
            @Result(property = "locked", column = "locked"),
            @Result(property = "driveTimeMillis", column = "drive_time_millis"),
            @Result(property = "lastActivity", column = "last_activity")
    })
    @Select("SELECT * FROM drivers")
    List<Driver> findAll();

    @Select("SELECT COUNT(player_id) FROM drivers")
    @ResultType(long.class)
    long count();

    @ResultMap("driverResultMap")
    @Select("SELECT * FROM drivers WHERE player_id = #{playerId}")
    Driver findByPlayerId(String playerId);

    @SuppressWarnings("unused")
    @ResultMap("driverResultMap")
    @Select("SELECT drivers.*, leaderboard_drivers.drive_time_millis FROM drivers INNER JOIN leaderboard_drivers on drivers.player_id = leaderboard_drivers.player_id WHERE leaderboard_drivers.car_id = #{carId} and leaderboard_drivers.session_id = #{sessionId}")
    List<Driver> findDriversBySessionAndCarId(Integer sessionId, Integer carId);

    @Insert("""
            INSERT INTO drivers (player_id, first_name, last_name, short_name, last_activity) 
            VALUES (#{playerId}, #{firstName}, #{lastName}, #{shortName}, #{lastActivity}) 
            ON CONFLICT(player_id) DO UPDATE SET first_name = excluded.first_name, last_name = excluded.last_name, short_name = excluded.short_name, last_activity = (SELECT CASE WHEN last_activity IS NULL OR last_activity < excluded.last_activity THEN excluded.last_activity ELSE last_activity END)
            """
    )
    void upsert(Driver driver);
}
