package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Session;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

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
            @Result(property = "driverCount", column = "driver_count"),
            @Result(property = "sessionDatetime", column = "session_datetime"),
            @Result(property = "fileChecksum", column = "file_checksum"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "fileDirectory", column = "file_directory"),
    })
    @Select("SELECT * FROM acc_leaderboard.sessions ORDER BY session_datetime DESC")
    List<Session> findAll();

    @ResultMap("sessionResultMap")
    @Select("SELECT * FROM acc_leaderboard.sessions WHERE id = #{id}")
    Session findById(Integer id);

    @ResultMap("sessionResultMap")
    @Select("SELECT * FROM acc_leaderboard.sessions WHERE file_checksum = #{fileChecksum}")
    Session findByFileChecksum(String fileChecksum);

    @Insert("INSERT INTO acc_leaderboard.sessions (session_type, race_weekend_index, server_name, track_id, wet_session, driver_count, session_datetime, file_checksum, file_name, file_directory) " +
            "VALUES (#{sessionType}, #{raceWeekendIndex}, #{serverName}, #{trackId}, #{wetSession}, #{driverCount}, #{sessionDatetime}, #{fileChecksum}, #{fileName}, #{fileDirectory})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Session session);
}
