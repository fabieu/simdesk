package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.entities.auth.DiscordUser;
import de.sustineo.simdesk.entities.typehandler.PermitsTypeHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DiscordUserMapper {
    @Results(id = "discordUserResultMap", value = {
            @Result(id = true, property = "userId", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "globalName", column = "global_name"),
            @Result(property = "permits", column = "permits", typeHandler = PermitsTypeHandler.class),
            @Result(property = "updateDatetime", column = "update_datetime")
    })
    @Select("SELECT * FROM users_discord WHERE user_id = #{userId}")
    DiscordUser findByUserId(Long userId);

    @Insert("INSERT INTO users_discord (user_id, username, global_name, permits, update_datetime) VALUES (#{userId}, #{username}, #{globalName},#{permits, typeHandler=de.sustineo.simdesk.entities.typehandler.PermitsTypeHandler}, #{updateDatetime}) ON CONFLICT(user_id) DO UPDATE SET username = #{username}, global_name = #{globalName}, update_datetime = #{updateDatetime}, permits = #{permits, typeHandler=de.sustineo.simdesk.entities.typehandler.PermitsTypeHandler}")
    boolean insert(DiscordUser user);
}
