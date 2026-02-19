package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import de.sustineo.simdesk.mybatis.mapper.StewardingTrackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class StewardingTrackService {
    private final StewardingTrackMapper trackMapper;

    public List<StewardingTrack> getAllTracks() {
        return trackMapper.findAll();
    }

    public StewardingTrack getTrackById(Integer id) {
        return trackMapper.findById(id);
    }

    @Transactional
    public void createTrack(StewardingTrack track) {
        trackMapper.insert(track);
    }

    @Transactional
    public void updateTrack(StewardingTrack track) {
        trackMapper.update(track);
    }

    @Transactional
    public void deleteTrack(Integer id) {
        trackMapper.delete(id);
    }
}
