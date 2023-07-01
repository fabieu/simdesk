package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Driver;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
@SuppressWarnings("unused")
public interface DriverMapper {
    @Results(id = "driverResultMap", value = {
            @Result(id = true, property = "playerId", column = "player_id"),
            @Result(property = "id", column = "id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "shortName", column = "short_name"),
            @Result(property = "locked", column = "locked"),
            @Result(property = "driveTimeMillis", column = "drive_time_millis"),
            @Result(property = "lastActivity", column = "last_activity")
    })
    @Select("SELECT * FROM acc_leaderboard.drivers")
    List<Driver> findAll();

    @ResultMap("driverResultMap")
    @Select("SELECT * FROM acc_leaderboard.drivers WHERE player_id = #{playerId}")
    Driver findByPlayerId(String playerId);

    @SuppressWarnings("unused")
    @ResultMap("driverResultMap")
    @Select("SELECT drivers.*, leaderboard_drivers.drive_time_millis FROM acc_leaderboard.drivers INNER JOIN acc_leaderboard.leaderboard_drivers on drivers.player_id = leaderboard_drivers.player_id WHERE leaderboard_drivers.car_id = #{carId} and leaderboard_drivers.session_id = #{sessionId}")
    List<Driver> findDriversBySessionAndCarId(Integer sessionId, Integer carId);

    @Insert("INSERT INTO acc_leaderboard.drivers (player_id, first_name, last_name, short_name, last_activity) " +
            "VALUES (#{playerId}, #{firstName}, #{lastName}, #{shortName}, #{lastActivity}) " +
            "ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name), short_name = VALUES(short_name), last_activity = VALUES(last_activity)"
    )
    void upsert(Driver driver);
}
