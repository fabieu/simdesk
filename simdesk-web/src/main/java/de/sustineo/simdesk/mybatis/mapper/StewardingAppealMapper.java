package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.Appeal;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardingAppealMapper {
    @Results(id = "stewardingAppealResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "decisionId", column = "decision_id"),
            @Result(property = "filedByUserId", column = "filed_by_user_id"),
            @Result(property = "filedByEntryId", column = "filed_by_entry_id"),
            @Result(property = "reason", column = "reason"),
            @Result(property = "status", column = "status"),
            @Result(property = "response", column = "response"),
            @Result(property = "respondedByUserId", column = "responded_by_user_id"),
            @Result(property = "filedAt", column = "filed_at"),
            @Result(property = "respondedAt", column = "responded_at"),
    })
    @Select("SELECT * FROM stewarding_appeal WHERE decision_id = #{decisionId} ORDER BY filed_at DESC")
    List<Appeal> findByDecisionId(String decisionId);

    @ResultMap("stewardingAppealResultMap")
    @Select("SELECT * FROM stewarding_appeal WHERE id = #{id}")
    Appeal findById(String id);

    @Insert("""
            INSERT INTO stewarding_appeal (id, decision_id, filed_by_user_id, filed_by_entry_id, reason, status, filed_at)
            VALUES (#{id}, #{decisionId}, #{filedByUserId}, #{filedByEntryId}, #{reason}, #{status}, CURRENT_TIMESTAMP)
            """)
    void insert(Appeal appeal);

    @Update("""
            UPDATE stewarding_appeal
            SET status = #{status}, response = #{response}, responded_by_user_id = #{respondedByUserId}, responded_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void updateResponse(String id, String status, String response, Integer respondedByUserId);
}
