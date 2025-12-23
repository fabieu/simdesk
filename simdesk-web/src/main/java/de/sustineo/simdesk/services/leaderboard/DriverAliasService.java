package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.DriverAlias;
import de.sustineo.simdesk.mybatis.mapper.DriverAliasMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile(SpringProfile.LEADERBOARD)
@Service
@RequiredArgsConstructor
public class DriverAliasService {
    private final DriverAliasMapper driverAliasMapper;

    public List<DriverAlias> getLatestAliasesByDriverId(String driverId, int limit) {
        return driverAliasMapper.findByDriverIdOrderByCreatedAtDesc(driverId, limit);
    }

    public void insertAlias(DriverAlias driverAlias) {
        driverAliasMapper.insert(driverAlias);
    }
}
