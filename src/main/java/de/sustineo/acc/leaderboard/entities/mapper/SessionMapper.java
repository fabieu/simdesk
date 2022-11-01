package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Session;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface SessionMapper {
    @Insert("INSERT INTO main.sessions () VALUES ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int save(Session session);
}
