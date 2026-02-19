package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.StewardingEntrylist;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardingEntrylistMapper {
    @Results(id = "stewardingEntrylistResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "raceWeekendId", column = "race_weekend_id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "uploadedAt", column = "uploaded_at"),
            @Result(property = "rawJson", column = "raw_json"),
    })
    @Select("SELECT * FROM stewarding_entrylist WHERE race_weekend_id = #{raceWeekendId}")
    List<StewardingEntrylist> findByRaceWeekendId(Integer raceWeekendId);

    @ResultMap("stewardingEntrylistResultMap")
    @Select("SELECT * FROM stewarding_entrylist WHERE session_id = #{sessionId}")
    List<StewardingEntrylist> findBySessionId(Integer sessionId);

    @ResultMap("stewardingEntrylistResultMap")
    @Select("SELECT * FROM stewarding_entrylist WHERE id = #{id}")
    StewardingEntrylist findById(Integer id);

    @Insert("""
            INSERT INTO stewarding_entrylist (race_weekend_id, session_id, uploaded_at, raw_json)
            VALUES (#{raceWeekendId}, #{sessionId}, CURRENT_TIMESTAMP, #{rawJson})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(StewardingEntrylist entrylist);

    @Delete("DELETE FROM stewarding_entrylist WHERE race_weekend_id = #{raceWeekendId}")
    void deleteByRaceWeekendId(Integer raceWeekendId);

    @Delete("DELETE FROM stewarding_entrylist WHERE session_id = #{sessionId}")
    void deleteBySessionId(Integer sessionId);
}
