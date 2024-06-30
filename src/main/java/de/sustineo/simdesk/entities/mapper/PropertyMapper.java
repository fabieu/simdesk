package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.entities.DynamicProperty;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface PropertyMapper {
    @Results(id = "propertyResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "key", column = "key"),
            @Result(property = "value", column = "value"),
            @Result(property = "description", column = "description"),
            @Result(property = "active", column = "active"),
            @Result(property = "updateDatetime", column = "update_datetime")
    })
    @Select("SELECT * FROM properties WHERE key = #{key} AND active = true")
    DynamicProperty findByKey(String key);

    @Insert("""
            INSERT INTO properties (key, value, update_datetime) VALUES (#{key}, #{value}, current_timestamp)
            ON CONFLICT(key) DO UPDATE SET value = excluded.value, description = excluded.description, update_datetime = excluded.update_datetime
            """)
    void update(String key, String value);
}
