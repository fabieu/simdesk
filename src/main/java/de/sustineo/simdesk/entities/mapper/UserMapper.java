package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.entities.auth.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    @Results(id = "userResultMap", value = {
            @Result(id = true, property = "userId", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password")
    })
    @Select("SELECT * FROM simdesk.user WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO simdesk.user (user_id, username, password) VALUES (#{userId}, #{username}, #{password}) ON CONFLICT(user_id) DO UPDATE SET username = #{username}, password = #{password}")
    void insertSystemUser(User user);
}
