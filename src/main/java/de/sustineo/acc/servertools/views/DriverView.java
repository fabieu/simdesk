package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.Driver;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.leaderboard.DriverService;
import de.sustineo.acc.servertools.utils.FormatUtils;
import de.sustineo.acc.servertools.views.filter.DriverFilter;
import de.sustineo.acc.servertools.views.filter.FilterUtils;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/drivers", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Drivers")
@AnonymousAllowed
public class DriverView extends VerticalLayout {
    private final DriverService driverService;

    public DriverView(DriverService driverService, ComponentUtils componentUtils) {
        this.driverService = driverService;

        setSizeFull();
        setPadding(false);

        addAndExpand(createDriverGrid());
        add(componentUtils.createFooter());
    }

    private Component createDriverGrid() {
        List<Driver> drivers = driverService.getWithDetails();

        Grid<Driver> grid = new Grid<>(Driver.class, false);
        Grid.Column<Driver> lastNameColumn = grid.addColumn(Driver::getLastName)
                .setHeader("Last Name")
                .setSortable(true);
        Grid.Column<Driver> firstNameColumn = grid.addColumn(Driver::getFirstName)
                .setHeader("First Name")
                .setSortable(true);
        Grid.Column<Driver> shortNameColumn = grid.addColumn(Driver::getShortName)
                .setHeader("Short Name")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> totalLapsColumn = grid.addColumn(Driver::getTotalLapsCount)
                .setHeader("Total Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> validLapsColumn = grid.addColumn(Driver::getValidLapsCount)
                .setHeader("Valid Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> invalidLapsColumn = grid.addColumn(Driver::getInvalidLapsCount)
                .setHeader("Invalid Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> validLapsPercentageColumn = grid.addColumn(driver -> FormatUtils.formatPercentage(driver.getValidLapsPercentage()))
                .setHeader("Valid Laps %")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> lastActivity = grid.addColumn(driver -> FormatUtils.formatDatetime(driver.getLastActivity()))
                .setHeader("Last Activity")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Driver::getLastActivity);

        GridListDataView<Driver> dataView = grid.setItems(drivers);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        DriverFilter driverFilter = new DriverFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(firstNameColumn).setComponent(FilterUtils.createFilterHeader(driverFilter::setFirstName));
        headerRow.getCell(lastNameColumn).setComponent(FilterUtils.createFilterHeader(driverFilter::setLastName));
        headerRow.getCell(shortNameColumn).setComponent(FilterUtils.createFilterHeader(driverFilter::setShortName));

        return grid;
    }
}
