package de.sustineo.simdesk.mapper;

import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.auth.UserType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    @Results(id = "userResultMap", value = {
            @Result(id = true, property = "id", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "type", column = "type"),
    })
    @Select("""
            SELECT * FROM "user" WHERE username = #{username}
            """)
    User findByUsername(String username);

    @Insert("""
            INSERT INTO "user" (user_id, username, password, type) VALUES (#{userId}, #{username}, #{password}, #{type})
            ON CONFLICT(user_id)
            DO UPDATE SET username = #{username}, password = #{password}
            """)
    void insertSystemUser(Integer userId, String username, String password, UserType type);

    @Insert("""
            INSERT INTO "user" (username, type) VALUES (#{discordUserId}, #{type})
            ON CONFLICT(username) DO NOTHING
            """)
    void insertDiscordUser(String discordUserId, UserType type);
}
