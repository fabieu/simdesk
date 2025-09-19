package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.Setting;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SettingMapper {
    @Results(id = "settingResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "key", column = "key"),
            @Result(property = "value", column = "value"),
            @Result(property = "active", column = "active"),
            @Result(property = "updateDatetime", column = "update_datetime")
    })
    @Select("SELECT * FROM settings WHERE key = #{key} AND active = true")
    Setting findActive(String key);

    @Insert("""
            INSERT INTO settings (key, value, update_datetime) VALUES (#{key}, #{value}, CURRENT_TIMESTAMP)
            ON CONFLICT(key) DO UPDATE
            SET value = excluded.value, update_datetime = excluded.update_datetime
            """)
    void update(String key, String value);
}
