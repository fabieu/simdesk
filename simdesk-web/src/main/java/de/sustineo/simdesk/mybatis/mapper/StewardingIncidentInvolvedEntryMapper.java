package de.sustineo.simdesk.mybatis.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface StewardingIncidentInvolvedEntryMapper {
    @Select("SELECT entry_id FROM stewarding_incident_involved_entry WHERE incident_id = #{incidentId}")
    List<Integer> findEntryIdsByIncidentId(Integer incidentId);

    @Insert("INSERT INTO stewarding_incident_involved_entry (incident_id, entry_id) VALUES (#{incidentId}, #{entryId})")
    void insert(Integer incidentId, Integer entryId);

    @Delete("DELETE FROM stewarding_incident_involved_entry WHERE incident_id = #{incidentId}")
    void deleteByIncidentId(Integer incidentId);
}
