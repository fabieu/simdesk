package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.Round;
import de.sustineo.simdesk.entities.stewarding.RoundSession;
import de.sustineo.simdesk.mybatis.mapper.RoundMapper;
import de.sustineo.simdesk.mybatis.mapper.RoundSessionMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingTrackMapper;
import de.sustineo.simdesk.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class RoundService {
    private final RoundMapper roundMapper;
    private final RoundSessionMapper sessionMapper;
    private final StewardingTrackMapper trackMapper;
    private final IdGenerator idGenerator;

    public List<Round> getAllRounds() {
        return roundMapper.findAll();
    }

    public Round getRoundById(String id) {
        Round round = roundMapper.findById(id);
        if (round != null && round.getTrackId() != null) {
            round.setTrack(trackMapper.findById(round.getTrackId()));
        }
        return round;
    }

    public List<Round> getRoundsBySeriesId(String seriesId) {
        return roundMapper.findBySeriesId(seriesId);
    }

    @Transactional
    public void createRound(Round round) {
        round.setId(idGenerator.generateRandomString(12));
        roundMapper.insert(round);
    }

    @Transactional
    public void updateRound(Round round) {
        roundMapper.update(round);
    }

    @Transactional
    public void deleteRound(String id) {
        roundMapper.delete(id);
    }

    public List<RoundSession> getSessionsByRoundId(String roundId) {
        return sessionMapper.findByRoundId(roundId);
    }

    public RoundSession getSessionById(String id) {
        return sessionMapper.findById(id);
    }

    @Transactional
    public void createSession(RoundSession session) {
        session.setId(idGenerator.generateRandomString(12));
        sessionMapper.insert(session);
    }

    @Transactional
    public void updateSession(RoundSession session) {
        sessionMapper.update(session);
    }

    @Transactional
    public void deleteSession(String id) {
        sessionMapper.delete(id);
    }
}
