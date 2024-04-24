package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.entities.Lap;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface LapMapper {
    @Results(id = "lapResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "driver", column = "driver_id", one = @One(select = "de.sustineo.simdesk.entities.mapper.DriverMapper.findByPlayerId")),
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "split1Millis", column = "split1_millis"),
            @Result(property = "split2Millis", column = "split2_millis"),
            @Result(property = "split3Millis", column = "split3_millis"),
            @Result(property = "valid", column = "valid"),
    })
    @Select("SELECT * FROM laps")
    List<Lap> findAll();

    @ResultMap("lapResultMap")
    @Select("""
            <script>
            SELECT * FROM laps WHERE session_id = #{sessionId} AND driver_id IN 
                <foreach item="item" index="index" collection="playerIds"
                    open="(" separator="," close=")">
                      #{item}
                </foreach>
                ORDER BY id ASC;
            </script>
            """)
    List<Lap> findBySessionAndDrivers(int sessionId, List<String> playerIds);

    @Select("SELECT COUNT(id) FROM laps")
    @ResultType(long.class)
    long count();

    @Insert("INSERT INTO laps (session_id, driver_id, car_group, car_model_id, lap_time_millis, split1_millis, split2_millis, split3_millis, valid) " +
            "VALUES (#{sessionId}, #{driver.playerId}, #{carGroup}, #{carModelId}, #{lapTimeMillis}, #{split1Millis}, #{split2Millis}, #{split3Millis}, #{valid})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Lap lap);
}
