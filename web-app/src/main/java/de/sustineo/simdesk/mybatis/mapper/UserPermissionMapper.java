package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.auth.UserPermission;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import org.apache.ibatis.annotations.*;
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
    List<UserPermission> findByUserId(Integer userId);

    @Select("SELECT role FROM user_permission WHERE user_id = #{userId}")
    List<UserRoleEnum> findRolesByUserId(Integer userId);

    @Insert("""
            INSERT INTO user_permission (user_id, role)
            VALUES (#{userId}, #{role})
            """)
    void insert(UserPermission userPermission);

    @Delete("DELETE FROM user_permission WHERE user_id = #{userId}")
    void deleteAllByUserId(Integer userId);
}
