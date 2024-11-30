package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Driver;

public class DriverFilter extends GridFilter {
    private final GridListDataView<Driver> dataView;

    private String playerId;
    private String fullName;
    private String visibility;

    public DriverFilter(GridListDataView<Driver> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
        this.dataView.refreshAll();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.dataView.refreshAll();
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
        this.dataView.refreshAll();
    }

    public boolean test(Driver driver) {
        boolean matchesServerName = matches(driver.getPlayerId(), playerId);
        boolean matchesTrackName = matches(driver.getFullName(), fullName);
        boolean matchesVisibility = matches(driver.getVisibility().name(), visibility);

        return matchesServerName && matchesTrackName && matchesVisibility;
    }
}
