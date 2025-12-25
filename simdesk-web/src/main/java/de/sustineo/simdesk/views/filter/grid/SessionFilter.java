package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.RaceTrack;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;

public class SessionFilter extends GridFilter<Session> {
    private String serverName;
    private RaceTrack raceTrack;
    private SessionType sessionType;

    public SessionFilter(GridListDataView<Session> dataView) {
        super(dataView);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
        refresh();
    }

    public void setRaceTrack(RaceTrack raceTrack) {
        this.raceTrack = raceTrack;
        refresh();
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        refresh();
    }

    protected boolean test(Session session) {
        boolean matchesServerName = matches(session.getServerName(), serverName);
        boolean matchesRaceTrack = matches(session.getRaceTrack(), raceTrack);
        boolean matchesSessionType = matches(session.getSessionType(), sessionType);

        return matchesServerName && matchesRaceTrack && matchesSessionType;
    }
}
