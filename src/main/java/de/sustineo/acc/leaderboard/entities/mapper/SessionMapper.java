package de.sustineo.acc.leaderboard.entities.mapper;

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

    @Insert("INSERT INTO main.sessions () VALUES ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int save(Session session);
}
