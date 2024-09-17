package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import de.sustineo.simdesk.entities.ranking.SessionRanking;
import org.apache.ibatis.annotations.*;
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
            @Result(property = "driver", column = "driver_id", one = @One(select = "de.sustineo.simdesk.entities.mapper.DriverMapper.findByPlayerId")),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
    })
    @Select("""
            SELECT laps.car_group, laps.car_model_id, laps.driver_id, sessions.track_id, MIN(laps.lap_time_millis) AS lap_time_millis
            FROM simdesk.laps
            LEFT JOIN simdesk.sessions ON laps.session_id = sessions.id
            WHERE valid IS TRUE
                AND sessions.session_datetime >= #{startTime}
                AND sessions.session_datetime <= #{endTime}
            GROUP BY laps.car_group, laps.car_model_id, sessions.track_id, laps.driver_id
            ORDER BY MIN(laps.lap_time_millis)
            """)
    List<GroupRanking> findAllTimeFastestLaps(Instant startTime, Instant endTime);

    @Results(id = "driverRankingResultMap", value = {
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "split1Millis", column = "split1_millis"),
            @Result(property = "split2Millis", column = "split2_millis"),
            @Result(property = "split3Millis", column = "split3_millis"),
            @Result(property = "driver", column = "driver_id", one = @One(select = "de.sustineo.simdesk.entities.mapper.DriverMapper.findByPlayerId")),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "session", column = "session_id", one = @One(select = "de.sustineo.simdesk.entities.mapper.SessionMapper.findById")),
    })
    @Select("""
            SELECT laps.*  FROM simdesk.laps
                INNER JOIN (SELECT laps.driver_id, laps.car_model_id, laps.car_group, sessions.track_id, MIN(laps.lap_time_millis) AS lap_time_millis FROM simdesk.laps
                             LEFT JOIN simdesk.sessions ON laps.session_id = sessions.id
                                 WHERE valid IS TRUE
                                   AND laps.car_group = #{carGroup}
                                   AND sessions.track_id = #{trackId}
                                   AND sessions.session_datetime >= #{startTime}
                                   AND sessions.session_datetime <= #{endTime}
                                 GROUP BY laps.driver_id, laps.car_model_id, laps.car_group, sessions.track_id) fastest_laps
                            ON laps.driver_id = fastest_laps.driver_id AND laps.car_model_id = fastest_laps.car_model_id AND
                               laps.car_group = fastest_laps.car_group AND track_id = fastest_laps.track_id AND
                               laps.lap_time_millis = fastest_laps.lap_time_millis
            """)
    List<DriverRanking> findAllTimeFastestLapsByTrack(CarGroup carGroup, String trackId, Instant startTime, Instant endTime);

    @Results(id = "leaderboardResultMap", value = {
            @Result(property = "session", column = "session_id", one = @One(select = "de.sustineo.simdesk.entities.mapper.SessionMapper.findById")),
            @Result(property = "ranking", column = "ranking"),
            @Result(property = "carId", column = "car_id"),
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "ballastKg", column = "ballast_kg"),
            @Result(property = "raceNumber", column = "race_number"),
            @Result(property = "drivers", column = "{sessionId=session_id, carId=car_id}", many = @Many(select = "de.sustineo.simdesk.entities.mapper.DriverMapper.findDriversBySessionAndCarId")),
            @Result(property = "bestLapTimeMillis", column = "best_lap_time_millis"),
            @Result(property = "bestSplit1Millis", column = "best_split1_millis"),
            @Result(property = "bestSplit2Millis", column = "best_split2_millis"),
            @Result(property = "bestSplit3Millis", column = "best_split3_millis"),
            @Result(property = "totalTimeMillis", column = "total_time_millis"),
            @Result(property = "lapCount", column = "lap_count")
    })
    @Select("SELECT * FROM simdesk.leaderboard_lines WHERE session_id = #{sessionId} ORDER BY ranking")
    List<SessionRanking> findLeaderboardLinesBySessionId(Integer sessionId);

    @ResultType(List.class)
    @Select("SELECT player_id FROM simdesk.leaderboard_drivers WHERE car_id = #{carId} AND session_id = #{sessionId}")
    List<String> findDriversBySessionAndCarId(Integer sessionId, Integer carId);
}
