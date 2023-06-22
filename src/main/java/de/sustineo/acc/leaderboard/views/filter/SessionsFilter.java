package de.sustineo.acc.leaderboard.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.acc.leaderboard.entities.Session;
import lombok.Data;

@Data
public class SessionsFilter {
    private final GridListDataView<Session> dataView;

    private String serverName;
    private String trackName;
    private String sessionDescription;

    public SessionsFilter(GridListDataView<Session> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
        this.dataView.refreshAll();
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
        this.dataView.refreshAll();
    }

    public void setSessionDescription(String sessionDescription) {
        this.sessionDescription = sessionDescription;
        this.dataView.refreshAll();
    }

    public boolean test(Session session) {
        boolean matchesServerName = matches(session.getServerName(), serverName);
        boolean matchesTrackName = matches(session.getTrackName(), trackName);
        boolean matchesSessionDescription = matches(session.getSessionType().getDescription(), sessionDescription);

        return matchesServerName && matchesTrackName && matchesSessionDescription;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
