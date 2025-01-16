package de.sustineo.simdesk.mapper;

import de.sustineo.simdesk.entities.auth.ApiKey;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserApiKeyMapper {
    @Results(id = "apiKeyResultMap", value = {
            @Result(id = true, property = "id", column = "api_key_id"),
            @Result(property = "user.user_id", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.password", column = "password"),
            @Result(property = "apiKey", column = "api_key"),
            @Result(property = "name", column = "name"),
            @Result(property = "active", column = "active"),
            @Result(property = "roles", column = "user_id", many = @Many(select = " de.sustineo.simdesk.mapper.UserPermissionMapper.findRolesByUserId")),
            @Result(property = "creationDatetime", column = "creation_datetime"),
    })
    @Select("""
            SELECT * FROM user_api_key
            LEFT JOIN "user" ON user_api_key.user_id = "user".user_id
            WHERE api_key = #{apiKey}
            """)
    ApiKey findByApiKey(String apiKey);

    @Delete("DELETE FROM user_api_key WHERE api_key_id = #{id}")
    void deleteById(ApiKey apiKey);

    @Update("UPDATE user_api_key SET active = #{active} WHERE api_key_id = #{id}")
    void updateStatus(ApiKey apiKey);
}
