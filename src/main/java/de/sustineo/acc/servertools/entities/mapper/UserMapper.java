package de.sustineo.acc.servertools.entities.mapper;

import de.sustineo.acc.servertools.entities.auth.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    @Results(id = "userResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password")
    })
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO users (username, password) VALUES (#{username}, #{password}) ON CONFLICT(username) DO UPDATE SET password = #{password}")
    boolean insert(String username, String password);
}
