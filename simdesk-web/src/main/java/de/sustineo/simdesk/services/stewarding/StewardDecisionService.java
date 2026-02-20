package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.IncidentStatus;
import de.sustineo.simdesk.entities.stewarding.StewardDecision;
import de.sustineo.simdesk.mybatis.mapper.StewardDecisionMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingIncidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class StewardDecisionService {
    private final StewardDecisionMapper decisionMapper;
    private final StewardingIncidentMapper incidentMapper;

    public StewardDecision getDecisionById(String id) {
        return decisionMapper.findById(id);
    }

    public StewardDecision getActiveDecisionByIncidentId(String incidentId) {
        List<StewardDecision> decisions = decisionMapper.findActiveByIncidentId(incidentId);
        return decisions.isEmpty() ? null : decisions.getFirst();
    }

    public List<StewardDecision> getDecisionHistory(String incidentId) {
        return decisionMapper.findByIncidentId(incidentId);
    }

    public List<StewardDecision> getDecisionsBySessionId(String sessionId) {
        return decisionMapper.findBySessionId(sessionId);
    }

    public List<StewardDecision> getManualDecisionsBySessionId(String sessionId) {
        return decisionMapper.findManualBySessionId(sessionId);
    }

    @Transactional
    public void makeDecision(StewardDecision decision) {
        decisionMapper.insert(decision);
        if (decision.getIncidentId() != null) {
            incidentMapper.updateStatus(decision.getIncidentId(), IncidentStatus.DECISION_MADE.name());
        }
    }

    @Transactional
    public void reviseDecision(String oldDecisionId, StewardDecision newDecision) {
        decisionMapper.deactivate(oldDecisionId);
        decisionMapper.insert(newDecision);
        decisionMapper.setSupersededBy(oldDecisionId, newDecision.getId());
    }
}
