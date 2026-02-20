package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.Appeal;
import de.sustineo.simdesk.entities.stewarding.AppealStatus;
import de.sustineo.simdesk.entities.stewarding.IncidentStatus;
import de.sustineo.simdesk.entities.stewarding.StewardDecision;
import de.sustineo.simdesk.mybatis.mapper.StewardDecisionMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingAppealMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingIncidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class StewardingAppealService {
    private final StewardingAppealMapper appealMapper;
    private final StewardingIncidentMapper incidentMapper;
    private final StewardDecisionMapper decisionMapper;

    public List<Appeal> getAppealsByDecisionId(String decisionId) {
        return appealMapper.findByDecisionId(decisionId);
    }

    public Appeal getAppealById(String id) {
        return appealMapper.findById(id);
    }

    @Transactional
    public void fileAppeal(Appeal appeal) {
        appealMapper.insert(appeal);
        StewardDecision decision = decisionMapper.findById(appeal.getDecisionId());
        if (decision != null && decision.getIncidentId() != null) {
            incidentMapper.updateStatus(decision.getIncidentId(), IncidentStatus.APPEALED.name());
        }
    }

    @Transactional
    public void reviewAppeal(String id, AppealStatus status, String response, Integer respondedByUserId) {
        appealMapper.updateResponse(id, status.name(), response, respondedByUserId);
        if (status == AppealStatus.ACCEPTED || status == AppealStatus.REJECTED) {
            Appeal appeal = appealMapper.findById(id);
            if (appeal != null) {
                StewardDecision decision = decisionMapper.findById(appeal.getDecisionId());
                if (decision != null && decision.getIncidentId() != null) {
                    incidentMapper.updateStatus(decision.getIncidentId(), IncidentStatus.APPEAL_REVIEWED.name());
                }
            }
        }
    }
}
