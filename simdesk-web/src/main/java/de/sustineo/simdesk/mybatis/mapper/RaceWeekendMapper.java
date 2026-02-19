package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.RaceWeekend;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RaceWeekendMapper {
    @Results(id = "raceWeekendResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "penaltyCatalogId", column = "penalty_catalog_id"),
            @Result(property = "discordWebhookUrl", column = "discord_webhook_url"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("SELECT * FROM stewarding_race_weekend ORDER BY start_date DESC")
    List<RaceWeekend> findAll();

    @ResultMap("raceWeekendResultMap")
    @Select("SELECT * FROM stewarding_race_weekend WHERE id = #{id}")
    RaceWeekend findById(Integer id);

    @ResultMap("raceWeekendResultMap")
    @Select("SELECT * FROM stewarding_race_weekend WHERE track_id = #{trackId}")
    List<RaceWeekend> findByTrackId(Integer trackId);

    @Insert("""
            INSERT INTO stewarding_race_weekend (title, description, track_id, penalty_catalog_id, discord_webhook_url, start_date, end_date, created_at, updated_at)
            VALUES (#{title}, #{description}, #{trackId}, #{penaltyCatalogId}, #{discordWebhookUrl}, #{startDate}, #{endDate}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(RaceWeekend weekend);

    @Update("""
            UPDATE stewarding_race_weekend
            SET title = #{title}, description = #{description}, track_id = #{trackId}, penalty_catalog_id = #{penaltyCatalogId},
                discord_webhook_url = #{discordWebhookUrl}, start_date = #{startDate}, end_date = #{endDate}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void update(RaceWeekend weekend);

    @Delete("DELETE FROM stewarding_race_weekend WHERE id = #{id}")
    void delete(Integer id);
}
