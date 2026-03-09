package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.Incident;
import de.sustineo.simdesk.entities.stewarding.IncidentStatus;
import de.sustineo.simdesk.mybatis.mapper.StewardingIncidentInvolvedEntryMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingIncidentMapper;
import de.sustineo.simdesk.services.IdGenerator;
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
    private final IdGenerator idGenerator;

    public List<Incident> getIncidentsBySessionId(String sessionId) {
        return incidentMapper.findBySessionId(sessionId);
    }

    public Incident getIncidentById(String id) {
        return incidentMapper.findById(id);
    }

    public List<Incident> getIncidentsBySessionIdAndStatus(String sessionId, IncidentStatus status) {
        return incidentMapper.findBySessionIdAndStatus(sessionId, status.name());
    }

    @Transactional
    public void createIncident(Incident incident, List<String> involvedEntryIds) {
        incident.setId(idGenerator.generateRandomString(12));
        incidentMapper.insert(incident);
        for (String entryId : involvedEntryIds) {
            involvedEntryMapper.insert(incident.getId(), entryId);
        }
    }

    @Transactional
    public void updateIncidentStatus(String id, IncidentStatus status) {
        incidentMapper.updateStatus(id, status.name());
    }

    public List<String> getInvolvedEntryIds(String incidentId) {
        return involvedEntryMapper.findEntryIdsByIncidentId(incidentId);
    }
}
