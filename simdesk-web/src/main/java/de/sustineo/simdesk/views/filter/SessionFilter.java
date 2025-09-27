package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;
import de.sustineo.simdesk.entities.Track;

public class SessionFilter extends GridFilter {
    private final GridListDataView<Session> dataView;

    private String serverName;
    private Track track;
    private SessionType sessionType;

    public SessionFilter(GridListDataView<Session> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
        this.dataView.refreshAll();
    }

    public void setTrack(Track track) {
        this.track = track;
        this.dataView.refreshAll();
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        this.dataView.refreshAll();
    }

    public boolean test(Session session) {
        boolean matchesServerName = matches(session.getServerName(), serverName);
        boolean matchesTrackName = matches(session.getTrack(), track);
        boolean matchesSessionType = matches(session.getSessionType(), sessionType);

        return matchesServerName && matchesTrackName && matchesSessionType;
    }
}
