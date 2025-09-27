package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Driver;

public class DriverFilter extends GridFilter<Driver> {
    private String driverId;
    private String realName;
    private String visibility;

    public DriverFilter(GridListDataView<Driver> dataView) {
        super(dataView);
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
        refresh();
    }

    public void setRealName(String realName) {
        this.realName = realName;
        refresh();
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
        refresh();
    }

    protected boolean test(Driver driver) {
        boolean matchesServerName = matches(driver.getId(), driverId);
        boolean matchesRealName = matches(driver.getRealName(), realName);
        boolean matchesVisibility = matches(driver.getVisibility().name(), visibility);

        return matchesServerName && matchesRealName && matchesVisibility;
    }
}
