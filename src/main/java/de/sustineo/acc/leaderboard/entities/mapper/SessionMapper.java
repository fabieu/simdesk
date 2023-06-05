package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Lap;
import de.sustineo.acc.leaderboard.entities.Session;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SessionMapper {
    @Results(id = "sessionResultMap", value = {
            @Result(property = "test", column = "test")
    })
    @Select("SELECT * FROM main.session")
    List<Session> findAll();

    @Insert("INSERT INTO acc_leaderboard.sessions () VALUES ()")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertSession(Session session);

    @Insert("INSERT INTO acc_leaderboard.laps () VALUES ()")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertLap(Lap lap);
}
