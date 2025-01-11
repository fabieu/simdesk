package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.Track;

public class SessionFilter extends GridFilter {
    private final GridListDataView<Session> dataView;

    private String serverName;
    private String trackName;
    private String sessionDescription;

    public SessionFilter(GridListDataView<Session> dataView) {
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
        boolean matchesTrackName = matches(Track.getTrackNameByAccId(session.getTrackId()), trackName);
        boolean matchesSessionDescription = matches(session.getSessionType().getDescription(), sessionDescription);

        return matchesServerName && matchesTrackName && matchesSessionDescription;
    }
}
