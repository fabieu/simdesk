package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.DriverAlias;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
@SuppressWarnings("unused")
public interface DriverAliasMapper {
    @Results(id = "driverAliasResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "driverId", column = "driver_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("SELECT * FROM driver_alias WHERE driver_id = #{driverId} ORDER BY created_at DESC LIMIT #{limit}")
    List<DriverAlias> findByDriverIdOrderByCreatedAtDesc(String driverId, int limit);

    @Insert("""
            INSERT INTO driver_alias (driver_id, first_name, last_name, created_at)
            VALUES (#{driverId}, #{firstName}, #{lastName}, #{createdAt})
            """)
    void insert(DriverAlias driverAlias);
}
