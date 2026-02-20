package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.StewardingEntrylistEntry;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardingEntrylistEntryMapper {
    @Results(id = "stewardingEntrylistEntryResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "entrylistId", column = "entrylist_id"),
            @Result(property = "raceNumber", column = "race_number"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "teamName", column = "team_name"),
            @Result(property = "displayName", column = "display_name"),
    })
    @Select("SELECT * FROM stewarding_entrylist_entry WHERE entrylist_id = #{entrylistId} ORDER BY race_number")
    List<StewardingEntrylistEntry> findByEntrylistId(String entrylistId);

    @ResultMap("stewardingEntrylistEntryResultMap")
    @Select("SELECT * FROM stewarding_entrylist_entry WHERE id = #{id}")
    StewardingEntrylistEntry findById(String id);

    @Insert("""
            INSERT INTO stewarding_entrylist_entry (id, entrylist_id, race_number, car_model_id, team_name, display_name)
            VALUES (#{id}, #{entrylistId}, #{raceNumber}, #{carModelId}, #{teamName}, #{displayName})
            """)
    void insert(StewardingEntrylistEntry entry);

    @Delete("DELETE FROM stewarding_entrylist_entry WHERE entrylist_id = #{entrylistId}")
    void deleteByEntrylistId(String entrylistId);
}
