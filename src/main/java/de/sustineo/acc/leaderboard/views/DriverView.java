package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.services.DriverService;
import de.sustineo.acc.leaderboard.utils.FormatUtils;
import de.sustineo.acc.leaderboard.views.filter.DriverFilter;
import de.sustineo.acc.leaderboard.views.filter.FilterUtils;
import org.springframework.boot.info.BuildProperties;

import java.util.List;

@Route(value = "drivers", layout = MainView.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Drivers")
@AnonymousAllowed
public class DriverView extends VerticalLayout {
    private final DriverService driverService;

    public DriverView(DriverService driverService, BuildProperties buildProperties) {
        this.driverService = driverService;

        addClassName("drivers-view");
        setSizeFull();

        addAndExpand(createDriverGrid());
        add(MainView.createFooterContent(buildProperties));
    }

    private Component createDriverGrid() {
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
                .setSortable(true);

        List<Driver> drivers = driverService.getWithDetails();
        GridListDataView<Driver> dataView = grid.setItems(drivers);
        DriverFilter driverFilter = new DriverFilter(dataView);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(firstNameColumn).setComponent(FilterUtils.createFilterHeader(driverFilter::setFirstName));
        headerRow.getCell(lastNameColumn).setComponent(FilterUtils.createFilterHeader(driverFilter::setLastName));
        headerRow.getCell(shortNameColumn).setComponent(FilterUtils.createFilterHeader(driverFilter::setShortName));

        return grid;
    }
}