package de.sustineo.acc.leaderboard.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.acc.leaderboard.entities.Driver;
import lombok.Data;

@Data
public class DriverFilter {
    private final GridListDataView<Driver> dataView;

    private String firstName;
    private String lastName;
    private String shortName;

    public DriverFilter(GridListDataView<Driver> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.dataView.refreshAll();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.dataView.refreshAll();
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        this.dataView.refreshAll();
    }

    public boolean test(Driver driver) {
        boolean matchesFirstName = matches(driver.getFirstName(), firstName);
        boolean matchesLastName = matches(driver.getLastName(), lastName);
        boolean matchesShortName = matches(driver.getShortName(), shortName);

        return matchesFirstName && matchesLastName && matchesShortName;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}