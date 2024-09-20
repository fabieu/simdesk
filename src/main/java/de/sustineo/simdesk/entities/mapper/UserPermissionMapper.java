package de.sustineo.simdesk.entities.mapper;

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
            @Result(property = "roleName", column = "role_name"),
            @Result(property = "insertDatetime", column = "insert_datetime"),
    })
    @Select("SELECT * FROM simdesk.user_permission WHERE user_id = #{userId}")
    List<UserPermission> findByUserId(Long userId);
}
