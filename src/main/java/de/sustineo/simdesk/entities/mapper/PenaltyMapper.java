package de.sustineo.simdesk.entities.mapper;


import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
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
    @Select("SELECT * FROM simdesk.penalty WHERE session_id = #{sessionId} AND car_id = #{carId} ORDER BY id")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM penalty WHERE session_id = #{sessionId} AND car_id = #{carId} ORDER BY id")
    List<Penalty> findBySessionAndCarId(int sessionId, int carId);

    @Insert("""
            INSERT INTO simdesk.penalty (session_id, car_id, reason, penalty, penalty_value, violation_lap, cleared_lap, post_race)
            VALUES (#{sessionId}, #{carId}, #{reason}, #{penalty}, #{penaltyValue}, #{violationLap}, #{clearedLap}, #{postRace})
            """)
    @Insert(databaseId = DatabaseVendor.SQLITE, value = """
            INSERT INTO simdesk.penalty (session_id, car_id, reason, penalty, penalty_value, violation_lap, cleared_lap, post_race)
            VALUES (#{sessionId}, #{carId}, #{reason}, #{penalty}, #{penaltyValue}, #{violationLap}, #{clearedLap}, #{postRace})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Penalty penalty);
}
