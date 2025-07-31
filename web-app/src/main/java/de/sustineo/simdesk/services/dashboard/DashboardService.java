package de.sustineo.simdesk.services.dashboard;

import de.sustineo.simdesk.mybatis.mapper.DashboardMapper;
import de.sustineo.simdesk.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardMapper dashboardMapper;
    private final IdGenerator idGenerator;
}
