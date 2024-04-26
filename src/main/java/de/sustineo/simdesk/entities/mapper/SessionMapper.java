package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Session;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
    })
    @Select("SELECT * FROM sessions ORDER BY session_datetime DESC")
    List<Session> findAll();

    @Select("SELECT COUNT(id) FROM sessions")
    @ResultType(long.class)
    long count();

    @ResultMap("sessionResultMap")
    @Select("SELECT * FROM sessions WHERE id = #{id} LIMIT 1")
    Session findById(Integer id);

    @ResultMap("sessionResultMap")
    @Select("SELECT * FROM sessions WHERE file_checksum = #{fileChecksum} LIMIT 1")
    Session findByFileChecksum(String fileChecksum);

    @Insert("""
            INSERT INTO sessions (session_type, race_weekend_index, server_name, track_id, wet_session, car_count, session_datetime, file_checksum, file_name, file_directory)
            VALUES (#{sessionType}, #{raceWeekendIndex}, #{serverName}, #{trackId}, #{wetSession}, #{carCount}, #{sessionDatetime}, #{fileChecksum}, #{fileName}, #{fileDirectory})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Session session);
}
