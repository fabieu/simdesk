package de.sustineo.simdesk.services;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;
import de.sustineo.simdesk.entities.search.SearchResult;
import de.sustineo.simdesk.entities.search.SearchType;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.views.enums.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles({ProfileManager.PROFILE_LEADERBOARD})
@SpringBootTest(classes = {
        SearchService.class,
        ProfileManager.class
})
class SearchServiceTest {
    @Autowired
    private SearchService searchService;

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        // Setup mock drivers
        Driver driver1 = Driver.builder()
                .id("driver1")
                .firstName("John")
                .lastName("Doe")
                .build();

        Driver driver2 = Driver.builder()
                .id("driver2")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(driverService.getAllDrivers()).thenReturn(Arrays.asList(driver1, driver2));

        // Setup mock sessions
        Session session1 = Session.builder()
                .id(1)
                .serverName("Test Server")
                .trackId("monza")
                .sessionType(SessionType.R)
                .fileChecksum("checksum1")
                .sessionDatetime(Instant.now())
                .build();

        when(sessionService.getAllBySessionTimeRange(any(TimeRange.class))).thenReturn(Collections.singletonList(session1));
    }

    @Test
    void testSearchDriverByName() {
        List<SearchResult> results = searchService.search("John", 0, 100);

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(result ->
                result.getType() == SearchType.DRIVER && result.getLabel().contains("John")
        );
    }

    @Test
    void testSearchDriverById() {
        List<SearchResult> results = searchService.search("driver1", 0, 100);

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(result ->
                result.getType() == SearchType.DRIVER && result.getId().equals("driver1")
        );
    }

    @Test
    void testSearchSessionByServerName() {
        List<SearchResult> results = searchService.search("Test Server", 0, 100);

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(result ->
                result.getType() == SearchType.SESSION && result.getLabel().contains("Test Server")
        );
    }

    @Test
    void testSearchWithEmptyQuery() {
        List<SearchResult> results = searchService.search("", 0, 100);

        assertThat(results).isEmpty();
    }

    @Test
    void testSearchWithNullQuery() {
        List<SearchResult> results = searchService.search(null, 0, 100);

        assertThat(results).isEmpty();
    }

    @Test
    void testSearchCaseInsensitive() {
        List<SearchResult> results = searchService.search("JOHN", 0, 100);

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(result ->
                result.getType() == SearchType.DRIVER &&
                        result.getLabel().contains("John")
        );
    }
}
