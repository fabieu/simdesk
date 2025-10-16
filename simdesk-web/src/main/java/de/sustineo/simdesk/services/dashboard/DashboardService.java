package de.sustineo.simdesk.services.dashboard;

import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import de.sustineo.simdesk.entities.livetiming.DashboardState;
import de.sustineo.simdesk.mybatis.mapper.DashboardMapper;
import de.sustineo.simdesk.services.IdGenerator;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.livetiming.LiveTimingStateService;
import lombok.NonNull;
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
    private static final int DASHBOARD_ID_LENGTH = 12;

    private final DashboardMapper dashboardMapper;
    private final LiveTimingStateService liveTimingStateService;
    private final IdGenerator idGenerator;
    private final SecurityService securityService;

    /**
     * Retrieves the current {@link DashboardState} for the given dashboard.
     * <p>
     * This method first attempts to obtain a live/in-memory state from
     * {@link LiveTimingStateService}. If no live state is available, it falls back to the persisted state returned by
     * {@link DashboardService#getByDashboardId}.
     *
     * @param dashboardId the identifier of the dashboard whose state should be retrieved (must not be {@code null})
     * @return the resolved {@link DashboardState}
     */
    public DashboardState getDashboardState(@NonNull String dashboardId) {
        DashboardState dashboardState = liveTimingStateService.getDashboardState(dashboardId);

        if (dashboardState == null) {
            dashboardState = getByDashboardId(dashboardId).getState();
        }

        return dashboardState;
    }

    /**
     * Creates a new {@link Dashboard} with a randomly generated identifier.
     * <p>
     * The identifier is produced via {@link IdGenerator#generateRandomString}
     * and assigned to the new dashboard instance. This method does not perform any
     * persistence; it only constructs and returns the object.
     *
     * @return a new {@link Dashboard} whose ID is a random string of length {@link DashboardService#DASHBOARD_ID_LENGTH}
     */
    public Dashboard createDashboard() {
        String dashboardId = idGenerator.generateRandomString(DASHBOARD_ID_LENGTH);
        return new Dashboard(dashboardId);
    }

    /**
     * Retrieves all {@link Dashboard} entities visible to the current user.
     * <p>
     * The visibility of dashboards depends on the user's roles:
     * <ul>
     *   <li>If the user has {@code ROLE_ADMIN}, all dashboards are returned.</li>
     *   <li>Otherwise, only dashboards marked as {@link Visibility#PUBLIC} are returned.</li>
     * </ul>
     *
     * @return a list of dashboards filtered according to the user's visibility rights;
     * never {@code null}, but may be empty if none are visible
     */
    public List<Dashboard> findAll() {
        Set<Visibility> visibilitySet = new HashSet<>();
        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
            visibilitySet.addAll(Arrays.asList(Visibility.values()));
        } else {
            visibilitySet.add(Visibility.PUBLIC);
        }

        return dashboardMapper.findAllByVisibility(visibilitySet);
    }

    /**
     * Retrieves a {@link Dashboard} by its unique identifier.-
     *
     * @param dashboardId the unique identifier of the dashboard to retrieve (must not be {@code null})
     * @return the {@link Dashboard} instance associated with the given ID,
     * or {@code null} if no dashboard exists for that ID
     */
    public Dashboard getByDashboardId(@NonNull String dashboardId) {
        return dashboardMapper.findById(dashboardId);
    }

    /**
     * Deletes a {@link Dashboard} by its unique identifier.
     * <p>
     * Only users with the {@code ADMIN} role are authorized to perform this operation,
     * as enforced by the {@link PreAuthorize} annotation.
     *
     * @param id the unique identifier of the dashboard to delete (must not be {@code null})
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteDashboard(@NonNull String id) {
        dashboardMapper.delete(id);
    }

    /**
     * Creates or updates (upserts) a {@link Dashboard}.
     * <p>
     * If a dashboard with the given identifier already exists, it will be updated;
     * otherwise, a new dashboard record will be created. This operation is restricted
     * to users with the {@code ADMIN} role, as enforced by the {@link PreAuthorize} annotation.
     *
     * @param dashboard the {@link Dashboard} instance to insert or update (must not be {@code null})
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void upsertDashboard(@NonNull Dashboard dashboard) {
        dashboardMapper.upsert(dashboard);
    }
}
