package de.sustineo.simdesk.mapper;

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
            @Result(property = "split1Millis", column = "split1_millis"),
            @Result(property = "split2Millis", column = "split2_millis"),
            @Result(property = "split3Millis", column = "split3_millis"),
            @Result(property = "valid", column = "valid"),
    })
    @Select("""
            <script>
            SELECT lap.*, driver.first_name, driver.last_name, driver.short_name, driver.visibility FROM lap
            LEFT JOIN driver ON lap.driver_id = driver.driver_id
            WHERE lap.session_id = #{sessionId} AND lap.driver_id IN
                <foreach item="item" index="index" collection="driverIds"
                    open="(" separator="," close=")">
                      #{item}
                </foreach>
            ORDER BY id ASC;
            </script>
            """)
    List<Lap> findBySessionIdAndDriverIds(int sessionId, List<String> driverIds);

    @ResultMap("lapResultMap")
    @Select("""
            SELECT lap.*, driver.first_name, driver.last_name, driver.short_name, driver.visibility FROM lap
            LEFT JOIN driver ON lap.driver_id = driver.driver_id
            WHERE session_id = #{sessionId}
            """)
    List<Lap> findBySessionId(Integer sessionId);

    @Insert("""
            INSERT INTO lap (session_id, driver_id, car_group, car_model_id, lap_time_millis, split1_millis, split2_millis, split3_millis, valid)
            VALUES (#{sessionId}, #{driver.id}, #{carGroup}, #{carModelId}, #{lapTimeMillis}, #{split1Millis}, #{split2Millis}, #{split3Millis}, #{valid})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Lap lap);
}
