package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
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
            SELECT lap.car_group, lap.car_model_id, lap.driver_id, session.track_id, MIN(lap.lap_time_millis) AS lap_time_millis
            FROM simdesk.lap
            LEFT JOIN simdesk.session ON lap.session_id = session.id
            WHERE valid IS TRUE
                AND session.session_datetime >= #{startTime}
                AND session.session_datetime <= #{endTime}
            GROUP BY lap.car_group, lap.car_model_id, session.track_id, lap.driver_id
            ORDER BY MIN(lap.lap_time_millis)
            """)
    @Select(databaseId = DatabaseVendor.SQLITE, value = """
            SELECT lap.car_group, lap.car_model_id, lap.driver_id, session.track_id, MIN(lap.lap_time_millis) AS lap_time_millis
            FROM lap
            LEFT JOIN session ON lap.session_id = session.id
            WHERE valid IS TRUE
                AND session.session_datetime >= #{startTime}
                AND session.session_datetime <= #{endTime}
            GROUP BY lap.car_group, lap.car_model_id, session.track_id, lap.driver_id
            ORDER BY MIN(lap.lap_time_millis)
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
            SELECT lap.*  FROM simdesk.lap
                INNER JOIN (SELECT lap.driver_id, lap.car_model_id, lap.car_group, session.track_id, MIN(lap.lap_time_millis) AS lap_time_millis FROM simdesk.lap
                             LEFT JOIN simdesk.session ON lap.session_id = session.id
                                 WHERE valid IS TRUE
                                   AND lap.car_group = #{carGroup}
                                   AND session.track_id = #{trackId}
                                   AND session.session_datetime >= #{startTime}
                                   AND session.session_datetime <= #{endTime}
                                 GROUP BY lap.driver_id, lap.car_model_id, lap.car_group, session.track_id) fastest_laps
                            ON lap.driver_id = fastest_laps.driver_id AND lap.car_model_id = fastest_laps.car_model_id AND
                               lap.car_group = fastest_laps.car_group AND track_id = fastest_laps.track_id AND
                               lap.lap_time_millis = fastest_laps.lap_time_millis
            """)
    @Select(databaseId = DatabaseVendor.SQLITE, value = """
            SELECT lap.*  FROM lap
                INNER JOIN (SELECT lap.driver_id, lap.car_model_id, lap.car_group, session.track_id, MIN(lap.lap_time_millis) AS lap_time_millis FROM lap
                             LEFT JOIN session ON lap.session_id = session.id
                                 WHERE valid IS TRUE
                                   AND lap.car_group = #{carGroup}
                                   AND session.track_id = #{trackId}
                                   AND session.session_datetime >= #{startTime}
                                   AND session.session_datetime <= #{endTime}
                                 GROUP BY lap.driver_id, lap.car_model_id, lap.car_group, session.track_id) fastest_laps
                            ON lap.driver_id = fastest_laps.driver_id AND lap.car_model_id = fastest_laps.car_model_id AND
                               lap.car_group = fastest_laps.car_group AND track_id = fastest_laps.track_id AND
                               lap.lap_time_millis = fastest_laps.lap_time_millis
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
    @Select("SELECT * FROM simdesk.leaderboard_line WHERE session_id = #{sessionId} ORDER BY ranking")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM leaderboard_line WHERE session_id = #{sessionId} ORDER BY ranking")
    List<SessionRanking> findLeaderboardLinesBySessionId(Integer sessionId);

    @ResultType(List.class)
    @Select("SELECT player_id FROM simdesk.leaderboard_driver WHERE car_id = #{carId} AND session_id = #{sessionId}")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT player_id FROM leaderboard_driver WHERE car_id = #{carId} AND session_id = #{sessionId}")
    List<String> findDriversBySessionAndCarId(Integer sessionId, Integer carId);
}
