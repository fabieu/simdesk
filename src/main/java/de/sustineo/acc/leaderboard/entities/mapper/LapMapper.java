package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Lap;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface LapMapper {
    @Results(id = "lapResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "driver", column = "driver_id", one = @One(select = "de.sustineo.acc.leaderboard.entities.mapper.DriverMapper.findById")),
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModel", column = "car_model"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "split1Millis", column = "split1_millis"),
            @Result(property = "split2Millis", column = "split2_millis"),
            @Result(property = "split3Millis", column = "split3_millis"),
            @Result(property = "valid", column = "valid"),
    })
    @Select("SELECT * FROM acc_leaderboard.laps")
    List<Lap> findAll();

    @Insert("INSERT INTO acc_leaderboard.laps (session_id, driver_id, car_group, car_model, lap_time_millis, split1_millis, split2_millis, split3_millis, valid) " +
            "VALUES (#{sessionId}, #{driver.playerId}, #{carGroup}, #{carModel}, #{lapTimeMillis}, #{split1Millis}, #{split2Millis}, #{split3Millis}, #{valid})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Lap lap);
}
