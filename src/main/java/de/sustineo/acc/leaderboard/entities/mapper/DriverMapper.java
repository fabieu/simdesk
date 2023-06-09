package de.sustineo.acc.leaderboard.entities.mapper;

import de.sustineo.acc.leaderboard.entities.Driver;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
@SuppressWarnings("unused")
public interface DriverMapper {
    @Results(id = "driverResultMap", value = {
            @Result(id = true, property = "playerId", column = "player_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "shortName", column = "short_name"),
    })
    @Select("SELECT * FROM acc_leaderboard.drivers")
    List<Driver> findAll();

    @ResultMap("driverResultMap")
    @Select("SELECT * FROM acc_leaderboard.drivers WHERE player_id = #{playerId}")
    Driver findById(String playerId);

    @Insert("INSERT INTO acc_leaderboard.drivers (player_id, first_name, last_name, short_name) " +
            "VALUES (#{playerId}, #{firstName}, #{lastName}, #{shortName}) " +
            "ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name), short_name = VALUES(short_name)"
    )
    void upsert(Driver driver);
}
