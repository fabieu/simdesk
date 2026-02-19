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
            @Result(property = "roundId", column = "round_id"),
            @Result(property = "uploadedAt", column = "uploaded_at"),
            @Result(property = "rawJson", column = "raw_json"),
    })
    @Select("SELECT * FROM stewarding_entrylist WHERE round_id = #{roundId}")
    List<StewardingEntrylist> findByRoundId(Integer roundId);

    @ResultMap("stewardingEntrylistResultMap")
    @Select("SELECT * FROM stewarding_entrylist WHERE id = #{id}")
    StewardingEntrylist findById(Integer id);

    @Insert("""
            INSERT INTO stewarding_entrylist (round_id, uploaded_at, raw_json)
            VALUES (#{roundId}, CURRENT_TIMESTAMP, #{rawJson})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(StewardingEntrylist entrylist);

    @Delete("DELETE FROM stewarding_entrylist WHERE round_id = #{roundId}")
    void deleteByRoundId(Integer roundId);
}
