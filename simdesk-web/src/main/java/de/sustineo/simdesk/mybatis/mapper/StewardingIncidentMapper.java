package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.Incident;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardingIncidentMapper {
    @Results(id = "stewardingIncidentResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "lap", column = "lap"),
            @Result(property = "timestampInSession", column = "timestamp_in_session"),
            @Result(property = "mapMarkerX", column = "map_marker_x"),
            @Result(property = "mapMarkerY", column = "map_marker_y"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "involvedCarsText", column = "involved_cars_text"),
            @Result(property = "status", column = "status"),
            @Result(property = "reportedByUserId", column = "reported_by_user_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("SELECT * FROM stewarding_incident WHERE session_id = #{sessionId} ORDER BY created_at DESC")
    List<Incident> findBySessionId(Integer sessionId);

    @ResultMap("stewardingIncidentResultMap")
    @Select("SELECT * FROM stewarding_incident WHERE id = #{id}")
    Incident findById(Integer id);

    @ResultMap("stewardingIncidentResultMap")
    @Select("SELECT * FROM stewarding_incident WHERE session_id = #{sessionId} AND status = #{status} ORDER BY created_at DESC")
    List<Incident> findBySessionIdAndStatus(Integer sessionId, String status);

    @Insert("""
            INSERT INTO stewarding_incident (session_id, title, description, lap, timestamp_in_session, map_marker_x, map_marker_y,
                video_url, involved_cars_text, status, reported_by_user_id, created_at, updated_at)
            VALUES (#{sessionId}, #{title}, #{description}, #{lap}, #{timestampInSession}, #{mapMarkerX}, #{mapMarkerY},
                #{videoUrl}, #{involvedCarsText}, #{status}, #{reportedByUserId}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Incident incident);

    @Update("UPDATE stewarding_incident SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(Integer id, String status);

    @Update("""
            UPDATE stewarding_incident
            SET title = #{title}, description = #{description}, lap = #{lap}, timestamp_in_session = #{timestampInSession},
                map_marker_x = #{mapMarkerX}, map_marker_y = #{mapMarkerY}, video_url = #{videoUrl},
                involved_cars_text = #{involvedCarsText}, status = #{status}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void update(Incident incident);
}
