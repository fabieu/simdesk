package de.sustineo.simdesk.mapper;

import de.sustineo.simdesk.entities.auth.UserPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserPermissionMapper {
    @Results(id = "userPermissionResultMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "role", column = "role"),
            @Result(property = "insertDatetime", column = "insert_datetime"),
    })
    @Select("SELECT * FROM user_permission WHERE user_id = #{userId}")
    List<UserPermission> findByUserId(Long userId);

    /**
     * Find roles by user ID. Used by {@link de.sustineo.simdesk.mapper.UserApiKeyMapper#findByApiKey(String)}.
     *
     * @param userId User ID
     * @return List of roles
     */
    @SuppressWarnings("unused")
    @Select("SELECT role FROM user_permission WHERE user_id = #{userId}")
    List<String> findRolesByUserId(Long userId);
}
