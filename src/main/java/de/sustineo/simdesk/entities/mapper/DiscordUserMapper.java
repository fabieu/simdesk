package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.entities.auth.DiscordUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DiscordUserMapper {
    @Insert("INSERT INTO users_discord (user_id, username, global_name, update_datetime) VALUES (#{userId}, #{username}, #{globalName}, #{updateDatetime}) ON CONFLICT(user_id) DO UPDATE SET username = #{username}, global_name = #{globalName}, update_datetime = #{updateDatetime}")
    boolean insert(DiscordUser user);
}
