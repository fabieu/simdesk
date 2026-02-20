package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.StewardingEntrylistDriver;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardingEntrylistDriverMapper {
    @Results(id = "stewardingEntrylistDriverResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "entryId", column = "entry_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "shortName", column = "short_name"),
            @Result(property = "steamId", column = "steam_id"),
            @Result(property = "category", column = "category"),
    })
    @Select("SELECT * FROM stewarding_entrylist_driver WHERE entry_id = #{entryId}")
    List<StewardingEntrylistDriver> findByEntryId(String entryId);

    @ResultMap("stewardingEntrylistDriverResultMap")
    @Select("SELECT * FROM stewarding_entrylist_driver WHERE id = #{id}")
    StewardingEntrylistDriver findById(String id);

    @Insert("""
            INSERT INTO stewarding_entrylist_driver (id, entry_id, first_name, last_name, short_name, steam_id, category)
            VALUES (#{id}, #{entryId}, #{firstName}, #{lastName}, #{shortName}, #{steamId}, #{category})
            """)
    void insert(StewardingEntrylistDriver driver);

    @Delete("DELETE FROM stewarding_entrylist_driver WHERE entry_id = #{entryId}")
    void deleteByEntryId(String entryId);
}
