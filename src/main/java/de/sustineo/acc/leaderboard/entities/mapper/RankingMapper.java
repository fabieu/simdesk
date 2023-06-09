package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Ranking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RankingMapper {
    @Results(id = "rankingResultMap", value = {
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "driverId", column = "driver_id"),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
    })
    @Select("SELECT laps.car_group, laps.car_model_id, laps.driver_id, sessions.track_id, MIN(laps.lap_time_millis) AS lap_time_millis FROM acc_leaderboard.laps LEFT JOIN acc_leaderboard.sessions ON laps.session_id = sessions.id WHERE valid IS TRUE GROUP BY laps.car_group, laps.car_model_id, sessions.track_id, laps.driver_id ORDER BY MIN(laps.lap_time_millis)")
    List<Ranking> findGlobalFastestLaps();
}
