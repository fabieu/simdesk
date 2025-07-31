package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.auth.ApiKey;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserApiKeyMapper {
    @Results(id = "apiKeyResultMap", value = {
            @Result(id = true, property = "id", column = "api_key_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "apiKey", column = "api_key"),
            @Result(property = "name", column = "name"),
            @Result(property = "active", column = "active"),
    })
    @Select("SELECT * FROM user_api_key WHERE user_id = #{userId}")
    List<ApiKey> findByUserId(Integer userId);

    @ResultMap("apiKeyResultMap")
    @Select("SELECT * FROM user_api_key WHERE api_key = #{apiKey} AND active = TRUE")
    ApiKey findActiveByApiKey(String apiKey);

    @Insert("""
            INSERT INTO user_api_key (user_id, api_key, name)
            VALUES (#{userId}, #{apiKey}, #{name})
            """)
    void insert(Integer userId, String apiKey, String name);

    @Update("UPDATE user_api_key SET active = #{active} WHERE api_key_id = #{id}")
    void updateStatus(ApiKey apiKey);

    @Delete("DELETE FROM user_api_key WHERE api_key_id = #{id}")
    void deleteById(ApiKey apiKey);
}
