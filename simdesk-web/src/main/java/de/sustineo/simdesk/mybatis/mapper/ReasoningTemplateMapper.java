package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.ReasoningTemplate;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ReasoningTemplateMapper {
    @Results(id = "reasoningTemplateResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "category", column = "category"),
            @Result(property = "templateText", column = "template_text"),
            @Result(property = "sortOrder", column = "sort_order"),
    })
    @Select("SELECT * FROM stewarding_reasoning_template ORDER BY category, sort_order")
    List<ReasoningTemplate> findAll();

    @ResultMap("reasoningTemplateResultMap")
    @Select("SELECT * FROM stewarding_reasoning_template WHERE id = #{id}")
    ReasoningTemplate findById(String id);

    @ResultMap("reasoningTemplateResultMap")
    @Select("SELECT * FROM stewarding_reasoning_template WHERE category = #{category} ORDER BY sort_order")
    List<ReasoningTemplate> findByCategory(String category);

    @Insert("""
            INSERT INTO stewarding_reasoning_template (id, name, category, template_text, sort_order)
            VALUES (#{id}, #{name}, #{category}, #{templateText}, #{sortOrder})
            """)
    void insert(ReasoningTemplate template);

    @Update("""
            UPDATE stewarding_reasoning_template
            SET name = #{name}, category = #{category}, template_text = #{templateText}, sort_order = #{sortOrder}
            WHERE id = #{id}
            """)
    void update(ReasoningTemplate template);

    @Delete("DELETE FROM stewarding_reasoning_template WHERE id = #{id}")
    void delete(String id);
}
