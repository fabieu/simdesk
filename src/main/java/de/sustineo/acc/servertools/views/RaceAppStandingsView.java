package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.raceapp.RaceAppResult;
import de.sustineo.acc.servertools.entities.raceapp.RaceAppSeries;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.NotificationService;
import de.sustineo.acc.servertools.services.raceapp.RaceAppService;
import de.sustineo.acc.servertools.views.filter.FilterUtils;
import de.sustineo.acc.servertools.views.filter.RaceAppStandingsFilter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

@Log
@Profile(ProfileManager.PROFILE_RACEAPP)
@Route(value = "/raceapp/series/:seriesId", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "RaceApp - Series Standings")
public class RaceAppStandingsView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_SERIES_ID = "seriesId";
    private final RaceAppService raceAppService;
    private final NotificationService notificationService;
    private final ComponentUtils componentUtils;

    public RaceAppStandingsView(RaceAppService raceAppService, NotificationService notificationService, ComponentUtils componentUtils) {
        this.raceAppService = raceAppService;
        this.notificationService = notificationService;
        this.componentUtils = componentUtils;

        setSizeFull();
        setPadding(false);
    }

    private Component createStandingsInformation(RaceAppSeries raceAppSeries) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setAlignItems(Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(raceAppSeries.getSettings().getName());

        layout.add(heading);

        return layout;
    }

    private Component createStandingsGrid(RaceAppSeries raceAppSeries) {
        List<RaceAppResult> raceAppResults = raceAppSeries.getResults();

        Grid<RaceAppResult> grid = new Grid<>(RaceAppResult.class, false);
        Grid.Column<RaceAppResult> positionColumn = grid.addColumn(RaceAppResult::getPosition)
                .setHeader("Position")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<RaceAppResult> positionInClassColumn = grid.addColumn(RaceAppResult::getPositionInClass)
                .setHeader("Position in class")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<RaceAppResult> teamNameColumn = grid.addColumn(RaceAppResult::getCarName)
                .setHeader("Team")
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<RaceAppResult> driverNamesColumn = grid.addColumn(RaceAppResult::getDriversString)
                .setHeader("Drivers")
                .setSortable(true);
        Grid.Column<RaceAppResult> carGroupColumn = grid.addColumn(RaceAppResult::getVehicleClass)
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<RaceAppResult> carModelColumn = grid.addColumn(RaceAppResult::getVehicleModel)
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<RaceAppResult> pointsColumn = grid.addColumn(RaceAppResult::getTotalPoints)
                .setHeader("Points")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        GridListDataView<RaceAppResult> dataView = grid.setItems(raceAppResults);
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setMultiSort(true, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        RaceAppStandingsFilter raceAppStandingsFilter = new RaceAppStandingsFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(teamNameColumn).setComponent(FilterUtils.createFilterHeader(raceAppStandingsFilter::setTeamName));
        headerRow.getCell(driverNamesColumn).setComponent(FilterUtils.createFilterHeader(raceAppStandingsFilter::setDriverNames));
        headerRow.getCell(carGroupColumn).setComponent(FilterUtils.createFilterHeader(raceAppStandingsFilter::setCarGroup));
        headerRow.getCell(carModelColumn).setComponent(FilterUtils.createFilterHeader(raceAppStandingsFilter::setCarModel));

        return grid;
    }

    public void beforeEnter(BeforeEnterEvent event) {
        final RouteParameters routeParameters = event.getRouteParameters();

        String seriesIdParameter = routeParameters.get(ROUTE_PARAMETER_SERIES_ID).orElseThrow();

        try {
            RaceAppSeries raceAppSeries = raceAppService.fetchSeries(Integer.parseInt(seriesIdParameter));

            add(createStandingsInformation(raceAppSeries));
            addAndExpand(createStandingsGrid(raceAppSeries));
            add(componentUtils.createFooter());
        } catch (HttpStatusCodeException e) {
            notificationService.showErrorNotification("HTTP " + e.getStatusCode() + " - Could not fetch series from RaceApp API");
        }
    }
}

