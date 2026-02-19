package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.RaceWeekendSession;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RaceWeekendSessionMapper {
    @Results(id = "raceWeekendSessionResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "raceWeekendId", column = "race_weekend_id"),
            @Result(property = "sessionType", column = "session_type"),
            @Result(property = "title", column = "title"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "createdAt", column = "created_at"),
    })
    @Select("SELECT * FROM stewarding_session WHERE race_weekend_id = #{raceWeekendId} ORDER BY sort_order")
    List<RaceWeekendSession> findByRaceWeekendId(Integer raceWeekendId);

    @ResultMap("raceWeekendSessionResultMap")
    @Select("SELECT * FROM stewarding_session WHERE id = #{id}")
    RaceWeekendSession findById(Integer id);

    @Insert("""
            INSERT INTO stewarding_session (race_weekend_id, session_type, title, start_time, end_time, sort_order, created_at)
            VALUES (#{raceWeekendId}, #{sessionType}, #{title}, #{startTime}, #{endTime}, #{sortOrder}, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(RaceWeekendSession session);

    @Update("""
            UPDATE stewarding_session
            SET race_weekend_id = #{raceWeekendId}, session_type = #{sessionType}, title = #{title},
                start_time = #{startTime}, end_time = #{endTime}, sort_order = #{sortOrder}
            WHERE id = #{id}
            """)
    void update(RaceWeekendSession session);

    @Delete("DELETE FROM stewarding_session WHERE id = #{id}")
    void delete(Integer id);
}
