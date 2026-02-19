package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.RaceWeekend;
import de.sustineo.simdesk.entities.stewarding.RaceWeekendSession;
import de.sustineo.simdesk.mybatis.mapper.PenaltyCatalogMapper;
import de.sustineo.simdesk.mybatis.mapper.RaceWeekendMapper;
import de.sustineo.simdesk.mybatis.mapper.RaceWeekendSessionMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingTrackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class RaceWeekendService {
    private final RaceWeekendMapper weekendMapper;
    private final RaceWeekendSessionMapper sessionMapper;
    private final StewardingTrackMapper trackMapper;
    private final PenaltyCatalogMapper catalogMapper;

    public List<RaceWeekend> getAllWeekends() {
        return weekendMapper.findAll();
    }

    public RaceWeekend getWeekendById(Integer id) {
        RaceWeekend weekend = weekendMapper.findById(id);
        if (weekend != null) {
            if (weekend.getTrackId() != null) {
                weekend.setTrack(trackMapper.findById(weekend.getTrackId()));
            }
            if (weekend.getPenaltyCatalogId() != null) {
                weekend.setPenaltyCatalog(catalogMapper.findById(weekend.getPenaltyCatalogId()));
            }
        }
        return weekend;
    }

    @Transactional
    public void createWeekend(RaceWeekend weekend) {
        weekendMapper.insert(weekend);
    }

    @Transactional
    public void updateWeekend(RaceWeekend weekend) {
        weekendMapper.update(weekend);
    }

    @Transactional
    public void deleteWeekend(Integer id) {
        weekendMapper.delete(id);
    }

    public List<RaceWeekendSession> getSessionsByWeekendId(Integer weekendId) {
        return sessionMapper.findByRaceWeekendId(weekendId);
    }

    public RaceWeekendSession getSessionById(Integer id) {
        return sessionMapper.findById(id);
    }

    @Transactional
    public void createSession(RaceWeekendSession session) {
        sessionMapper.insert(session);
    }

    @Transactional
    public void updateSession(RaceWeekendSession session) {
        sessionMapper.update(session);
    }

    @Transactional
    public void deleteSession(Integer id) {
        sessionMapper.delete(id);
    }
}
