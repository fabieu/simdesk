package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.PenaltyDefinition;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface PenaltyDefinitionMapper {
    @Results(id = "penaltyDefinitionResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "catalogId", column = "catalog_id"),
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "category", column = "category"),
            @Result(property = "sessionType", column = "session_type"),
            @Result(property = "defaultPenalty", column = "default_penalty"),
            @Result(property = "severity", column = "severity"),
            @Result(property = "sortOrder", column = "sort_order"),
    })
    @Select("SELECT * FROM stewarding_penalty_definition WHERE catalog_id = #{catalogId} ORDER BY sort_order, name")
    List<PenaltyDefinition> findByCatalogId(String catalogId);

    @ResultMap("penaltyDefinitionResultMap")
    @Select("SELECT * FROM stewarding_penalty_definition WHERE id = #{id}")
    PenaltyDefinition findById(String id);

    @ResultMap("penaltyDefinitionResultMap")
    @Select("""
            SELECT * FROM stewarding_penalty_definition
            WHERE catalog_id = #{catalogId} AND (session_type = #{sessionType} OR session_type = 'ALL')
            ORDER BY sort_order, name
            """)
    List<PenaltyDefinition> findByCatalogIdAndSessionType(String catalogId, String sessionType);

    @ResultMap("penaltyDefinitionResultMap")
    @Select("SELECT * FROM stewarding_penalty_definition WHERE catalog_id = #{catalogId} ORDER BY category, sort_order")
    List<PenaltyDefinition> findByCatalogIdGroupedByCategory(String catalogId);

    @Insert("""
            INSERT INTO stewarding_penalty_definition (id, catalog_id, code, name, description, category, session_type, default_penalty, severity, sort_order)
            VALUES (#{id}, #{catalogId}, #{code}, #{name}, #{description}, #{category}, #{sessionType}, #{defaultPenalty}, #{severity}, #{sortOrder})
            """)
    void insert(PenaltyDefinition definition);

    @Update("""
            UPDATE stewarding_penalty_definition
            SET catalog_id = #{catalogId}, code = #{code}, name = #{name}, description = #{description}, category = #{category},
                session_type = #{sessionType}, default_penalty = #{defaultPenalty}, severity = #{severity}, sort_order = #{sortOrder}
            WHERE id = #{id}
            """)
    void update(PenaltyDefinition definition);

    @Delete("DELETE FROM stewarding_penalty_definition WHERE id = #{id}")
    void delete(String id);
}
