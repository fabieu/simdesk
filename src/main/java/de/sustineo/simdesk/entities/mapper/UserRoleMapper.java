package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserRoleMapper {
    @Results(id = "userRoleResultMap", value = {
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "discordRoleId", column = "discord_role_id"),
    })
    @Select("SELECT * FROM simdesk.user_role")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM user_role")
    List<UserRole> findAll();

    @Update("UPDATE simdesk.user_role SET discord_role_id = #{discordRoleId} WHERE name = #{name}")
    @Update(databaseId = DatabaseVendor.SQLITE, value = "UPDATE user_role SET discord_role_id = #{discordRoleId} WHERE name = #{name}")
    void update(UserRole userRole);
}
