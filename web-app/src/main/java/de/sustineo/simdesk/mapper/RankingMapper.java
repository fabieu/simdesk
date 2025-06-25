package de.sustineo.simdesk.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Component
@Mapper
public interface RankingMapper {
    @Results(id = "groupRankingResultMap", value = {
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "driver.id", column = "driver_id"),
            @Result(property = "driver.firstName", column = "first_name"),
            @Result(property = "driver.lastName", column = "last_name"),
            @Result(property = "driver.shortName", column = "short_name"),
            @Result(property = "driver.visibility", column = "visibility"),
            @Result(property = "trackId", column = "track_id"),
    })
    @Select(databaseId = "sqlite", value = """
            SELECT lap.car_group, lap.car_model_id, lap.lap_time_millis, driver.*, session.track_id
            FROM lap
                    INNER JOIN session ON lap.session_id = session.id
                    INNER JOIN driver ON lap.driver_id = driver.driver_id
                    INNER JOIN (
                    SELECT lap.car_model_id, lap.driver_id, session.track_id, lap.car_group, MIN(lap.lap_time_millis) AS min_lap_time_millis
                        FROM lap
                            INNER JOIN session ON lap.session_id = session.id
                        WHERE lap.valid IS TRUE
                          AND session.session_datetime >= #{from}
                          AND session.session_datetime <= #{to}
                        GROUP BY session.track_id, lap.car_group
                    ) fastest_laps
                    ON lap.car_model_id = fastest_laps.car_model_id
                       AND lap.driver_id = fastest_laps.driver_id
                       AND session.track_id = fastest_laps.track_id
                       AND lap.car_group = fastest_laps.car_group
                       AND lap.lap_time_millis = fastest_laps.min_lap_time_millis
                    WHERE lap.valid IS TRUE
                      AND session.session_datetime >= #{from}
                      AND session.session_datetime <= #{to}
            ORDER BY lap.car_group, session.track_id;
            """)
    @Select(databaseId = "postgres", value = """
            SELECT DISTINCT ON (session.track_id, lap.car_group)
                   lap.car_group,
                   lap.car_model_id,
                   lap.lap_time_millis,
                   driver.*,
                   session.track_id
            FROM lap
                     INNER JOIN session ON lap.session_id = session.id
                     INNER JOIN driver ON lap.driver_id = driver.driver_id
            WHERE lap.valid IS TRUE
              AND session.session_datetime >= #{from}
              AND session.session_datetime <= #{to}
            ORDER BY lap.car_group, session.track_id, lap.lap_time_millis;
            """)
    List<GroupRanking> findAllTimeFastestLaps(Instant from, Instant to);

    @Results(id = "driverRankingResultMap", value = {
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "sector1Millis", column = "sector1_millis"),
            @Result(property = "sector2Millis", column = "sector2_millis"),
            @Result(property = "sector3Millis", column = "sector3_millis"),
            @Result(property = "driver.id", column = "driver_id"),
            @Result(property = "driver.firstName", column = "first_name"),
            @Result(property = "driver.lastName", column = "last_name"),
            @Result(property = "driver.shortName", column = "short_name"),
            @Result(property = "driver.visibility", column = "visibility"),
            @Result(property = "session.sessionType", column = "session_type"),
            @Result(property = "session.trackId", column = "track_id"),
            @Result(property = "session.serverName", column = "server_name"),
            @Result(property = "session.sessionDatetime", column = "session_datetime"),
            @Result(property = "session.fileChecksum", column = "file_checksum"),
    })
    @Select(databaseId = "sqlite", value = """
            SELECT lap.*, driver.*, session.*
            FROM lap
                     INNER JOIN session ON lap.session_id = session.id
                     INNER JOIN driver ON lap.driver_id = driver.driver_id
                     INNER JOIN (SELECT lap.driver_id, lap.car_model_id, MIN(lap.lap_time_millis) AS lap_time_millis
                                 FROM lap
                                          INNER JOIN session ON lap.session_id = session.id
                                 WHERE lap.valid = TRUE
                                   AND lap.car_group = #{carGroup}
                                   AND session.track_id = #{trackId}
                                   AND session.session_datetime >= #{from}
                                   AND session.session_datetime <= #{to}
                                 GROUP BY lap.driver_id, lap.car_model_id) fastest_laps
                                ON lap.driver_id = fastest_laps.driver_id
                                    AND lap.car_model_id = fastest_laps.car_model_id
                                    AND lap.lap_time_millis = fastest_laps.lap_time_millis
            WHERE lap.valid = TRUE
              AND lap.car_group = #{carGroup}
              AND session.track_id = #{trackId}
              AND session.session_datetime >= #{from}
              AND session.session_datetime <= #{to}
            """)
    @Select(databaseId = "postgres", value = """
            SELECT DISTINCT ON (lap.driver_id, lap.car_model_id)
                   lap.*,
                   driver.*,
                   session.*
            FROM lap
                     INNER JOIN session ON lap.session_id = session.id
                     INNER JOIN driver ON lap.driver_id = driver.driver_id
            WHERE lap.valid = TRUE
              AND lap.car_group = #{carGroup}
              AND session.track_id = #{trackId}
              AND session.session_datetime >= #{from}
              AND session.session_datetime <= #{to}
            ORDER BY lap.driver_id, lap.car_model_id, lap.lap_time_millis;
            """)
    List<DriverRanking> findAllTimeFastestLapsByTrack(CarGroup carGroup, String trackId, Instant from, Instant to);
}
