package de.sustineo.simdesk.mybatis.mapper;

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
            @Result(property = "driver.id", column = "driver_id"),
            @Result(property = "driver.firstName", column = "first_name"),
            @Result(property = "driver.lastName", column = "last_name"),
            @Result(property = "driver.shortName", column = "short_name"),
            @Result(property = "driver.visibility", column = "visibility"),
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "sector1Millis", column = "sector1_millis"),
            @Result(property = "sector2Millis", column = "sector2_millis"),
            @Result(property = "sector3Millis", column = "sector3_millis"),
            @Result(property = "valid", column = "valid"),
    })
    @Select("""
            SELECT lap.*, driver.first_name, driver.last_name, driver.short_name, driver.visibility FROM lap
            LEFT JOIN driver ON lap.driver_id = driver.driver_id
            WHERE session_id = #{sessionId}
            """)
    List<Lap> findBySessionId(Integer sessionId);

    @ResultMap("lapResultMap")
    @Select("Select * from lap where driver_id = #{driverId}")
    List<Lap> findByDriverId(String driverId);

    @Insert("""
            INSERT INTO lap (session_id, driver_id, car_group, car_model_id, lap_time_millis, sector1_millis, sector2_millis, sector3_millis, valid)
            VALUES (#{sessionId}, #{driver.id}, #{carGroup}, #{carModelId}, #{lapTimeMillis}, #{sector1Millis}, #{sector2Millis}, #{sector3Millis}, #{valid})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Lap lap);
}
