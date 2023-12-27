package de.sustineo.acc.servertools.entities.mapper;


import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.Penalty;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Component
@Mapper
public interface PenaltyMapper {
    @Results(id = "penaltyResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "carId", column = "car_id"),
            @Result(property = "reason", column = "reason"),
            @Result(property = "penalty", column = "penalty"),
            @Result(property = "penaltyValue", column = "penalty_value"),
            @Result(property = "violationLap", column = "violation_lap"),
            @Result(property = "clearedLap", column = "cleared_lap"),
            @Result(property = "postRace", column = "post_race")
    })
    @Select("SELECT * FROM acc_leaderboard.penalties")
    List<Penalty> findBySessionAndCarId(int sessionId, int carId);

    @Insert("INSERT INTO acc_leaderboard.penalties (session_id, car_id, reason, penalty, penalty_value, violation_lap, cleared_lap, post_race) " +
            "VALUES (#{sessionId}, #{carId}, #{reason}, #{penalty}, #{penaltyValue}, #{violationLap}, #{clearedLap}, #{postRace})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Penalty penalty);
}
