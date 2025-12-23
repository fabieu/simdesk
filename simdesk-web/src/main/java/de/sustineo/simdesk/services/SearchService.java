package de.sustineo.simdesk.services;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.search.SearchResult;
import de.sustineo.simdesk.entities.search.SearchType;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.enums.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Profile(SpringProfile.LEADERBOARD)
@Service
@RequiredArgsConstructor
public class SearchService {
    private static final int MAX_RESULTS_PER_TYPE = 20;

    private final DriverService driverService;
    private final SessionService sessionService;

    public List<SearchResult> search(String searchTerm, int offset, int limit) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<SearchResult> results = new ArrayList<>();

        // Search drivers
        List<Driver> drivers = driverService.getAllDrivers();
        results.addAll(drivers.stream()
                .filter(driver ->
                        driver.getFullName().toLowerCase().contains(lowerSearchTerm) ||
                                driver.getId().toLowerCase().contains(lowerSearchTerm)
                )
                .sorted(Comparator.comparing(Driver::getFullName))
                .limit(MAX_RESULTS_PER_TYPE)
                .map(this::createDriverSearchResult)
                .toList());

        // Search sessions - limit to recent sessions for performance
        List<Session> sessions = sessionService.getAllBySessionTimeRange(TimeRange.ALL_TIME);
        results.addAll(sessions.stream()
                .filter(session ->
                        (session.getServerName() != null && session.getServerName().toLowerCase().contains(lowerSearchTerm))
                )
                .sorted(Comparator.comparing(Session::getSessionDatetime))
                .limit(MAX_RESULTS_PER_TYPE)
                .map(this::createSessionSearchResult)
                .toList());

        return results.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private SearchResult createDriverSearchResult(Driver driver) {
        return SearchResult.builder()
                .type(SearchType.DRIVER)
                .id(driver.getId())
                .label(driver.getFullName())
                .build();
    }

    private SearchResult createSessionSearchResult(Session session) {
        return SearchResult.builder()
                .type(SearchType.SESSION)
                .id(session.getFileChecksum())
                .label(session.getServerName() + " " + FormatUtils.formatDatetime(session.getSessionDatetime()))
                .build();
    }
}
