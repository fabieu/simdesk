package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;
import de.sustineo.simdesk.entities.Track;

public class SessionFilter extends GridFilter<Session> {
    private String serverName;
    private Track track;
    private SessionType sessionType;

    public SessionFilter(GridListDataView<Session> dataView) {
        super(dataView);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
        refresh();
    }

    public void setTrack(Track track) {
        this.track = track;
        refresh();
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        refresh();
    }

    protected boolean test(Session session) {
        boolean matchesServerName = matches(session.getServerName(), serverName);
        boolean matchesTrackName = matches(session.getTrack(), track);
        boolean matchesSessionType = matches(session.getSessionType(), sessionType);

        return matchesServerName && matchesTrackName && matchesSessionType;
    }
}
