package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Driver;

public class DriverFilter extends GridFilter<Driver> {
    private String driverId;
    private String firstName;
    private String lastName;
    private String shortName;
    private String visibility;

    public DriverFilter(GridListDataView<Driver> dataView) {
        super(dataView);
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
        refresh();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        refresh();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        refresh();
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        refresh();
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
        refresh();
    }

    protected boolean test(Driver driver) {
        boolean matchesServerName = matches(driver.getId(), driverId);
        boolean matchesFirstName = matches(driver.getFirstName(), firstName);
        boolean matchesLastName = matches(driver.getLastName(), lastName);
        boolean matchesShortName = matches(driver.getShortName(), shortName);
        boolean matchesVisibility = matches(driver.getVisibility().name(), visibility);

        return matchesServerName && matchesFirstName && matchesLastName && matchesShortName && matchesVisibility;
    }
}
