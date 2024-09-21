package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Session;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Component
@Mapper
public interface SessionMapper {
    @Results(id = "sessionResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "sessionType", column = "session_type"),
            @Result(property = "raceWeekendIndex", column = "race_weekend_index"),
            @Result(property = "serverName", column = "server_name"),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "wetSession", column = "wet_session"),
            @Result(property = "carCount", column = "car_count"),
            @Result(property = "sessionDatetime", column = "session_datetime"),
            @Result(property = "fileChecksum", column = "file_checksum"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "fileDirectory", column = "file_directory"),
            @Result(property = "fileContent", column = "file_content"),
    })
    @Select("""
            SELECT *
            FROM simdesk.session
            WHERE session_datetime >= #{startTime}
              AND session_datetime <= #{endTime}
            ORDER BY session_datetime DESC
            """)
    List<Session> findAllByTimeRange(Instant startTime, Instant endTime);

    @Select("SELECT COUNT(id) FROM simdesk.session")
    @ResultType(long.class)
    long count();

    @SuppressWarnings("unused")
    @ResultMap("sessionResultMap")
    @Select("SELECT * FROM simdesk.session WHERE id = #{id} LIMIT 1")
    Session findById(Integer id);


    @ResultMap("sessionResultMap")
    @Select("SELECT * FROM simdesk.session WHERE file_checksum = #{fileChecksum} LIMIT 1")
    Session findByFileChecksum(String fileChecksum);

    @Insert("""
            INSERT INTO simdesk.session (session_type, race_weekend_index, server_name, track_id, wet_session, car_count, session_datetime, file_checksum, file_name, file_content)
            VALUES (#{sessionType}, #{raceWeekendIndex}, #{serverName}, #{trackId}, #{wetSession}, #{carCount}, #{sessionDatetime}, #{fileChecksum}, #{fileName}, #{fileContent})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Session session);
}
