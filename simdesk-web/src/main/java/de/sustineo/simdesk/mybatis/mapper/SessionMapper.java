package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.Session;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
            @Result(property = "carCount", column = "car_count"),
            @Result(property = "sessionDatetime", column = "session_datetime"),
            @Result(property = "fileChecksum", column = "file_checksum"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "fileDirectory", column = "file_directory"),
            @Result(property = "fileContent", column = "file_content"),
            @Result(property = "insertDatetime", column = "insert_datetime"),
    })
    @Select("SELECT * FROM session ORDER BY session_datetime DESC")
    List<Session> findAll();

    @ResultMap("sessionResultMap")
    @Select("""
            SELECT *
            FROM session
            WHERE session_datetime >= #{startTime}
              AND session_datetime <= #{endTime}
            ORDER BY session_datetime DESC
            """)
    List<Session> findAllBySessionTimeRange(Instant startTime, Instant endTime);

    @ResultMap("sessionResultMap")
    @Select("""
            SELECT *
            FROM session
            WHERE insert_datetime >= #{startTime}
              AND insert_datetime <= #{endTime}
            ORDER BY insert_datetime DESC
            """)
    List<Session> findAllByInsertTimeRange(Instant startTime, Instant endTime);

    @ResultMap("sessionResultMap")
    @Select("SELECT * FROM session WHERE file_checksum = #{fileChecksum}")
    Session findByFileChecksum(String fileChecksum);

    @Insert("""
            INSERT INTO session (session_type, race_weekend_index, server_name, track_id, wet_session, car_count, session_datetime, file_checksum, file_name, file_content)
            VALUES (#{sessionType}, #{raceWeekendIndex}, #{serverName}, #{trackId}, #{wetSession}, #{carCount}, #{sessionDatetime}, #{fileChecksum}, #{fileName}, #{fileContent})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Session session);

    @ResultMap("sessionResultMap")
    @Select("""
            SELECT *
            FROM session
            WHERE session_datetime >= #{from}
              AND session_datetime <= #{to}
              AND id IN (SELECT DISTINCT session_id FROM leaderboard_driver WHERE driver_id = #{driverId})
            ORDER BY session_datetime DESC
            """)
    List<Session> findAllByTimeRangeAndDriverId(Instant from, Instant to, String driverId);

    @ResultMap("sessionResultMap")
    @Select("""
            SELECT *
            FROM session
            WHERE id IN (SELECT DISTINCT session_id FROM leaderboard_driver WHERE driver_id = #{driverId})
            ORDER BY session_datetime DESC
            """)
    List<Session> findAllByDriverId(String driverId);
}
