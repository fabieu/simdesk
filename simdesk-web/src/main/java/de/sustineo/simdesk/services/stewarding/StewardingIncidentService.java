package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.Incident;
import de.sustineo.simdesk.entities.stewarding.IncidentStatus;
import de.sustineo.simdesk.mybatis.mapper.StewardingIncidentInvolvedEntryMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingIncidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class StewardingIncidentService {
    private final StewardingIncidentMapper incidentMapper;
    private final StewardingIncidentInvolvedEntryMapper involvedEntryMapper;

    public List<Incident> getIncidentsBySessionId(Integer sessionId) {
        return incidentMapper.findBySessionId(sessionId);
    }

    public Incident getIncidentById(Integer id) {
        return incidentMapper.findById(id);
    }

    public List<Incident> getIncidentsBySessionIdAndStatus(Integer sessionId, IncidentStatus status) {
        return incidentMapper.findBySessionIdAndStatus(sessionId, status.name());
    }

    @Transactional
    public void createIncident(Incident incident, List<Integer> involvedEntryIds) {
        incidentMapper.insert(incident);
        for (Integer entryId : involvedEntryIds) {
            involvedEntryMapper.insert(incident.getId(), entryId);
        }
    }

    @Transactional
    public void updateIncidentStatus(Integer id, IncidentStatus status) {
        incidentMapper.updateStatus(id, status.name());
    }

    public List<Integer> getInvolvedEntryIds(Integer incidentId) {
        return involvedEntryMapper.findEntryIdsByIncidentId(incidentId);
    }
}
