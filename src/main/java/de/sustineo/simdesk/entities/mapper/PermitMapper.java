package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.permit.Permit;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ProfileManager.PROFILE_DISCORD)
@Component
@Mapper
public interface PermitMapper {
    @Cacheable(value = "permits", key = "#userId")
    @Results(id = "permitResultMap", value = {
            @Result(id = true, property = "userId", column = "user_id"),
            @Result(property = "permit", column = "permit"),
            @Result(property = "updateDatetime", column = "update_datetime")
    })
    @Select("SELECT * FROM permit WHERE user_id = #{userId}")
    List<Permit> findByUserId(Long userId);

    @CacheEvict(value = "permits", key = "#permit.userId")
    @Insert("INSERT INTO permit (user_id, permit, update_datetime) VALUES (#{userId}, #{permit}, #{updateDatetime}) ON CONFLICT(user_id, permit) DO UPDATE SET update_datetime = #{updateDatetime}")
    boolean insert(Permit permit);

    @CacheEvict(value = "permits", key = "#userId")
    @Delete("DELETE FROM permit WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}
