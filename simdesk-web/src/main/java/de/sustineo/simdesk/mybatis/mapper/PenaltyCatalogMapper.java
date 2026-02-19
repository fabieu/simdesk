package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface PenaltyCatalogMapper {
    @Results(id = "penaltyCatalogResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
    })
    @Select("SELECT * FROM stewarding_penalty_catalog ORDER BY name")
    List<PenaltyCatalog> findAll();

    @ResultMap("penaltyCatalogResultMap")
    @Select("SELECT * FROM stewarding_penalty_catalog WHERE id = #{id}")
    PenaltyCatalog findById(Integer id);

    @Insert("""
            INSERT INTO stewarding_penalty_catalog (name, description, created_at, updated_at)
            VALUES (#{name}, #{description}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(PenaltyCatalog catalog);

    @Update("""
            UPDATE stewarding_penalty_catalog
            SET name = #{name}, description = #{description}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void update(PenaltyCatalog catalog);

    @Delete("DELETE FROM stewarding_penalty_catalog WHERE id = #{id}")
    void delete(Integer id);
}
