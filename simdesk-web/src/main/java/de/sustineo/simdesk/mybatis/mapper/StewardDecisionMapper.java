package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.StewardDecision;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardDecisionMapper {
    @Results(id = "stewardDecisionResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "incidentId", column = "incident_id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "decidedByUserId", column = "decided_by_user_id"),
            @Result(property = "penaltyDefinitionId", column = "penalty_definition_id"),
            @Result(property = "customPenalty", column = "custom_penalty"),
            @Result(property = "reasoning", column = "reasoning"),
            @Result(property = "reasoningTemplateId", column = "reasoning_template_id"),
            @Result(property = "isNoAction", column = "is_no_action"),
            @Result(property = "penalizedEntryId", column = "penalized_entry_id"),
            @Result(property = "penalizedCarText", column = "penalized_car_text"),
            @Result(property = "decidedAt", column = "decided_at"),
            @Result(property = "supersededById", column = "superseded_by_id"),
            @Result(property = "isActive", column = "is_active"),
    })
    @Select("SELECT * FROM stewarding_decision WHERE id = #{id}")
    StewardDecision findById(String id);

    @ResultMap("stewardDecisionResultMap")
    @Select("SELECT * FROM stewarding_decision WHERE incident_id = #{incidentId} AND is_active = true")
    List<StewardDecision> findActiveByIncidentId(String incidentId);

    @ResultMap("stewardDecisionResultMap")
    @Select("SELECT * FROM stewarding_decision WHERE incident_id = #{incidentId} ORDER BY decided_at DESC")
    List<StewardDecision> findByIncidentId(String incidentId);

    @ResultMap("stewardDecisionResultMap")
    @Select("SELECT * FROM stewarding_decision WHERE session_id = #{sessionId} AND is_active = true ORDER BY decided_at DESC")
    List<StewardDecision> findBySessionId(String sessionId);

    @ResultMap("stewardDecisionResultMap")
    @Select("SELECT * FROM stewarding_decision WHERE incident_id IS NULL AND session_id = #{sessionId} AND is_active = true ORDER BY decided_at DESC")
    List<StewardDecision> findManualBySessionId(String sessionId);

    @Insert("""
            INSERT INTO stewarding_decision (id, incident_id, session_id, decided_by_user_id, penalty_definition_id, custom_penalty,
                reasoning, reasoning_template_id, is_no_action, penalized_entry_id, penalized_car_text, decided_at, superseded_by_id, is_active)
            VALUES (#{id}, #{incidentId}, #{sessionId}, #{decidedByUserId}, #{penaltyDefinitionId}, #{customPenalty},
                #{reasoning}, #{reasoningTemplateId}, #{isNoAction}, #{penalizedEntryId}, #{penalizedCarText}, CURRENT_TIMESTAMP, #{supersededById}, #{isActive})
            """)
    void insert(StewardDecision decision);

    @Update("UPDATE stewarding_decision SET is_active = false WHERE id = #{id}")
    void deactivate(String id);

    @Update("UPDATE stewarding_decision SET superseded_by_id = #{supersededById} WHERE id = #{id}")
    void setSupersededBy(String id, String supersededById);
}
