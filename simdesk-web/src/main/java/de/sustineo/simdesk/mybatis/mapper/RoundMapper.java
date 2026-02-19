package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.Round;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RoundMapper {
    @Results(id = "roundResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "seriesId", column = "series_id"),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("SELECT * FROM stewarding_race_weekend ORDER BY start_date DESC")
    List<Round> findAll();

    @ResultMap("roundResultMap")
    @Select("SELECT * FROM stewarding_race_weekend WHERE id = #{id}")
    Round findById(Integer id);

    @ResultMap("roundResultMap")
    @Select("SELECT * FROM stewarding_race_weekend WHERE series_id = #{seriesId} ORDER BY start_date")
    List<Round> findBySeriesId(Integer seriesId);

    @ResultMap("roundResultMap")
    @Select("SELECT * FROM stewarding_race_weekend WHERE track_id = #{trackId}")
    List<Round> findByTrackId(Integer trackId);

    @Insert("""
            INSERT INTO stewarding_race_weekend (series_id, track_id, title, start_date, end_date, created_at, updated_at)
            VALUES (#{seriesId}, #{trackId}, #{title}, #{startDate}, #{endDate}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Round round);

    @Update("""
            UPDATE stewarding_race_weekend
            SET series_id = #{seriesId}, track_id = #{trackId}, title = #{title},
                start_date = #{startDate}, end_date = #{endDate}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void update(Round round);

    @Delete("DELETE FROM stewarding_race_weekend WHERE id = #{id}")
    void delete(Integer id);
}
