package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Lap;
import de.sustineo.acc.leaderboard.entities.Session;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LapMapper {
    @Results(id = "lapResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModel", column = "car_model"),
            @Result(property = "driver.firstName", column = "driver_first_name"),
            @Result(property = "driver.lastName", column = "driver_last_name"),
            @Result(property = "driver.shortName", column = "driver_short_name"),
            @Result(property = "driver.playerId", column = "driver_player_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "split1Millis", column = "split1_millis"),
            @Result(property = "split2Millis", column = "split2_millis"),
            @Result(property = "split3Millis", column = "split3_millis"),
            @Result(property = "valid", column = "valid"),

    })
    @Select("SELECT * FROM acc_leaderboard.sessions")
    List<Session> findAll();

    @Insert("INSERT INTO acc_leaderboard.laps (session_id, car_group, car_model, driver_first_name, driver_last_name, driver_short_name, driver_player_id, lap_time_millis, split1_millis, split2_millis, split3_millis, valid) " +
            "VALUES (#{sessionId}, #{carGroup}, #{carModel}, #{driver.firstName}, #{driver.lastName}, #{driver.shortName}, #{driver.playerId}, #{lapTimeMillis}, #{split1Millis}, #{split2Millis}, #{split3Millis}, #{valid})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Lap lap);
}
