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
    @Select("""
            SELECT lap.car_group, lap.car_model_id, lap.lap_time_millis AS lap_time_millis, driver.*, session.track_id
            FROM lap
                     INNER JOIN (SELECT lap.driver_id, lap.car_group, lap.car_model_id, session.track_id, MIN(lap.lap_time_millis) AS lap_time_millis
                                 FROM lap
                                    LEFT JOIN session ON lap.session_id = session.id
                                 WHERE valid IS TRUE
                                    AND session.session_datetime >= #{from}
                                    AND session.session_datetime <= #{to}
                                 GROUP BY lap.car_group, lap.car_model_id, session.track_id, lap.driver_id) fastest_laps
                                ON lap.car_group = fastest_laps.car_group
                                    AND lap.car_model_id = fastest_laps.car_model_id
                                    AND lap.driver_id = fastest_laps.driver_id
                                    AND lap.lap_time_millis = fastest_laps.lap_time_millis
                     LEFT JOIN driver ON lap.driver_id = driver.driver_id
                     LEFT JOIN session ON lap.session_id = session.id
            ORDER BY lap_time_millis;
            """)
    List<GroupRanking> findAllTimeFastestLaps(Instant from, Instant to);

    @Results(id = "driverRankingResultMap", value = {
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "split1Millis", column = "split1_millis"),
            @Result(property = "split2Millis", column = "split2_millis"),
            @Result(property = "split3Millis", column = "split3_millis"),
            @Result(property = "driver.id", column = "driver_id"),
            @Result(property = "driver.firstName", column = "first_name"),
            @Result(property = "driver.lastName", column = "last_name"),
            @Result(property = "driver.shortName", column = "short_name"),
            @Result(property = "driver.visibility", column = "visibility"),
            @Result(property = "session.sessionType", column = "session_type"),
            @Result(property = "session.serverName", column = "server_name"),
            @Result(property = "session.sessionDatetime", column = "session_datetime"),
    })
    @Select("""
            SELECT lap.*, driver.*, session.*
            FROM lap
            INNER JOIN (SELECT lap.driver_id, lap.car_model_id, MIN(lap.lap_time_millis) AS lap_time_millis
                FROM lap
                LEFT JOIN session ON lap.session_id = session.id
                WHERE valid IS TRUE
                  AND lap.car_group = #{carGroup}
                  AND session.track_id = #{trackId}
                  AND session.session_datetime >= #{from}
                  AND session.session_datetime <= #{to}
                GROUP BY lap.driver_id, lap.car_model_id, lap.car_group, session.track_id
            ) fastest_laps ON lap.driver_id = fastest_laps.driver_id
                          AND lap.car_model_id = fastest_laps.car_model_id
                          AND lap.lap_time_millis = fastest_laps.lap_time_millis
            LEFT JOIN driver ON lap.driver_id = driver.driver_id
            LEFT JOIN session ON lap.session_id = session.id
            """)
    List<DriverRanking> findAllTimeFastestLapsByTrack(CarGroup carGroup, String trackId, Instant from, Instant to);
}
