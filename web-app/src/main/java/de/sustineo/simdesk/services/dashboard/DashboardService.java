package de.sustineo.simdesk.services.dashboard;

import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import de.sustineo.simdesk.mybatis.mapper.DashboardMapper;
import de.sustineo.simdesk.services.IdGenerator;
import de.sustineo.simdesk.services.auth.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardMapper dashboardMapper;
    private final IdGenerator idGenerator;
    private final SecurityService securityService;

    public List<Dashboard> findAll() {
        Set<Visibility> visibilitySet = new HashSet<>();
        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
            visibilitySet.addAll(Arrays.asList(Visibility.values()));
        } else {
            visibilitySet.add(Visibility.PUBLIC);
        }

        return dashboardMapper.findAllByVisibility(visibilitySet);
    }

    public Dashboard getByDashboardId(String dashboardId) {
        return dashboardMapper.findById(dashboardId);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteDashboard(String id) {
        dashboardMapper.delete(id);
    }
}
