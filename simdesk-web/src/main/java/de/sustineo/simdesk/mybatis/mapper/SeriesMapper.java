package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.Series;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface SeriesMapper {
    @Results(id = "seriesResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "discordWebhookUrl", column = "discord_webhook_url"),
            @Result(property = "videoUrlEnabled", column = "video_url_enabled"),
            @Result(property = "penaltyCatalogId", column = "penalty_catalog_id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("SELECT * FROM stewarding_series ORDER BY start_date DESC")
    List<Series> findAll();

    @ResultMap("seriesResultMap")
    @Select("SELECT * FROM stewarding_series WHERE id = #{id}")
    Series findById(Integer id);

    @Insert("""
            INSERT INTO stewarding_series (title, description, discord_webhook_url, video_url_enabled, penalty_catalog_id, start_date, end_date, created_at, updated_at)
            VALUES (#{title}, #{description}, #{discordWebhookUrl}, #{videoUrlEnabled}, #{penaltyCatalogId}, #{startDate}, #{endDate}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Series series);

    @Update("""
            UPDATE stewarding_series
            SET title = #{title}, description = #{description}, discord_webhook_url = #{discordWebhookUrl},
                video_url_enabled = #{videoUrlEnabled}, penalty_catalog_id = #{penaltyCatalogId},
                start_date = #{startDate}, end_date = #{endDate}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void update(Series series);

    @Delete("DELETE FROM stewarding_series WHERE id = #{id}")
    void delete(Integer id);
}
