package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Visibility;

public class DriverFilter extends GridFilter<Driver> {
    private String driverId;
    private String realName;
    private String shortName;
    private Visibility visibility;

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

    public void setShortName(String shortName) {
        this.shortName = shortName;
        refresh();
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
        refresh();
    }

    protected boolean test(Driver driver) {
        boolean matchesServerName = matches(driver.getId(), driverId);
        boolean matchesRealName = matches(driver.getRealName(), realName);
        boolean matchesShortName = matches(driver.getShortName(), shortName);
        boolean matchesVisibility = matches(driver.getVisibility(), visibility);

        return matchesServerName && matchesRealName && matchesShortName && matchesVisibility;
    }
}
