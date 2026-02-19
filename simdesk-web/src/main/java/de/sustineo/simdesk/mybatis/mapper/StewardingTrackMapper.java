package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardingTrackMapper {
    @Results(id = "stewardingTrackResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "country", column = "country"),
            @Result(property = "mapImageUrl", column = "map_image_url"),
            @Result(property = "mapMetadata", column = "map_metadata"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("SELECT * FROM stewarding_track ORDER BY name")
    List<StewardingTrack> findAll();

    @ResultMap("stewardingTrackResultMap")
    @Select("SELECT * FROM stewarding_track WHERE id = #{id}")
    StewardingTrack findById(Integer id);

    @Insert("""
            INSERT INTO stewarding_track (name, country, map_image_url, map_metadata, created_at, updated_at)
            VALUES (#{name}, #{country}, #{mapImageUrl}, #{mapMetadata}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(StewardingTrack track);

    @Update("""
            UPDATE stewarding_track
            SET name = #{name}, country = #{country}, map_image_url = #{mapImageUrl}, map_metadata = #{mapMetadata}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void update(StewardingTrack track);

    @Delete("DELETE FROM stewarding_track WHERE id = #{id}")
    void delete(Integer id);
}
