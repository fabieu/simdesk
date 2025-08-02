package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import de.sustineo.simdesk.entities.livetiming.DashboardState;
import de.sustineo.simdesk.mybatis.typehandler.JsonTypeHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Mapper
@SuppressWarnings("unused")
public interface DashboardMapper {
    @Results(id = "dashboardResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "visibility", column = "visibility"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "broadcastUrl", column = "broadcast_url"),
            @Result(property = "startDatetime", column = "start_datetime"),
            @Result(property = "endDatetime", column = "end_datetime"),
            @Result(property = "state", column = "state", typeHandler = JsonTypeHandler.class),
            @Result(property = "stateDatetime", column = "state_datetime"),
            @Result(property = "updateDatetime", column = "update_datetime"),
            @Result(property = "createDatetime", column = "create_datetime"),
    })
    @Select("""
            <script>
                SELECT * FROM dashboard
                WHERE visibility IN
                    <foreach item='item' collection='visibility' open='(' separator=',' close=')'>
                        #{item}
                    </foreach>
                ORDER BY state_datetime DESC NULLS LAST, create_datetime DESC
            </script>
            """)
    List<Dashboard> findAllByVisibility(Collection<Visibility> visibility);

    @ResultMap("dashboardResultMap")
    @Select("SELECT * FROM dashboard WHERE id = #{id}")
    Dashboard findById(String id);

    @Insert("""
            INSERT INTO dashboard (id, visibility, name, description, broadcast_url, start_datetime, end_datetime)
            VALUES (#{id}, #{visibility}, #{name}, #{description}, #{broadcastUrl}, #{startDatetime}, #{endDatetime})
            """)
    @Options(keyProperty = "id", keyColumn = "id")
    void insert(Dashboard dashboard);

    @Update("""
            UPDATE dashboard
            SET state = (#{state}),
                state_datetime = NOW(),
            WHERE id = #{id}
            """)
    void updateState(String id, DashboardState state);
}
