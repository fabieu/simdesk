package de.sustineo.simdesk.views;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.streams.DownloadHandler;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.services.leaderboard.LapService;
import de.sustineo.simdesk.services.leaderboard.PenaltyService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.views.components.SessionComponentFactory;
import de.sustineo.simdesk.views.generators.InvalidLapPartNameGenerator;
import de.sustineo.simdesk.views.renderers.LapRenderer;
import de.sustineo.simdesk.views.renderers.SessionDetailsRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Profile(SpringProfile.LEADERBOARD)
@Route(value = "/leaderboard/sessions/:fileChecksum/details/:carId")
@AnonymousAllowed
@RequiredArgsConstructor
public class LeaderboardSessionCarDetailsView extends BaseView {
    private final SessionService sessionService;
    private final LapService lapService;
    private final PenaltyService penaltyService;
    private final SecurityService securityService;
    private final DriverService driverService;

    private final SessionComponentFactory sessionComponentFactory;

    private List<Lap> laps = new ArrayList<>();
    private List<Penalty> penalties = new ArrayList<>();

    @Override
    public String getPageTitle() {
        return "Leaderboard - Session Car Details";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();
        int carId = Integer.parseInt(routeParameters.get(ROUTE_PARAMETER_CAR_ID).orElseThrow());

        Session session = sessionService.getByFileChecksum(fileChecksum);
        if (session == null) {
            throw new NotFoundException("Session with file checksum " + fileChecksum + " does not exist.");
        }

        List<String> driverIds = driverService.getDriverIdsBySessionIdAndCarId(session.getId(), carId);
        if (driverIds == null || driverIds.isEmpty()) {
            throw new NotFoundException("No car found in session with file checksum " + fileChecksum + " and car id " + carId);
        }

        laps = lapService.getBySessionIdAndDriverIds(session.getId(), driverIds);
        penalties = penaltyService.findBySessionIdAndCarId(session.getId(), carId).stream()
                .filter(Penalty::isValid)
                .collect(Collectors.toList());

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        removeAll();

        add(createViewHeader());
        add(sessionComponentFactory.createSessionInformation(session));
        addAndExpand(createTabSheet(fileChecksum, carId));
    }

    private Component createTabSheet(String fileChecksum, int carId) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);

        tabSheet.add(createTab("Laps", laps.size()), createLapsGrid());
        tabSheet.add(createTab("Penalties", penalties.size()), createPenaltyGrid());

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
            tabSheet.add(createTab("Statistics"), createStatisticsLayout(fileChecksum, carId));
        }

        return tabSheet;
    }

    private Component createLapsGrid() {
        Grid<Lap> grid = new Grid<>(Lap.class, false);
        grid.addColumn(LitRenderer.of("${index + 1}"))
                .setHeader("#")
                .setWidth(GRID_RANKING_WIDTH)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFrozen(true);
        grid.addColumn(lap -> AccCar.getGroupById(lap.getCarModelId()))
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(lap -> AccCar.getModelById(lap.getCarModelId()))
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getLapTimeMillis);
        grid.addColumn(LapRenderer.createDriverRenderer())
                .setHeader("Driver")
                .setSortable(true);
        grid.addColumn(LapRenderer.createLapTimeRenderer())
                .setHeader("Lap Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold")
                .setSortable(true)
                .setComparator(Lap::getLapTimeMillis);
        grid.addColumn(LapRenderer.createSector1Renderer())
                .setHeader("Sector 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getSector1Millis);
        grid.addColumn(LapRenderer.createSector2Renderer())
                .setHeader("Sector 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getSector2Millis);
        grid.addColumn(LapRenderer.createSector3Renderer())
                .setHeader("Sector 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getSector3Millis);

        grid.setItems(laps);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setPartNameGenerator(new InvalidLapPartNameGenerator());

        return grid;
    }

    private Component createPenaltyGrid() {
        Grid<Penalty> grid = new Grid<>(Penalty.class, false);
        grid.addColumn(LitRenderer.of("${index + 1}"))
                .setHeader("#")
                .setWidth(GRID_RANKING_WIDTH)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFrozen(true);
        grid.addColumn(Penalty::getPenaltyAbbreviation)
                .setHeader("Penalty")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(Penalty::getReasonDescription)
                .setHeader("Reason")
                .setSortable(true);
        grid.addColumn(Penalty::getViolationLapCorrected)
                .setHeader("Violation in lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(Penalty::getClearedLapCorrected)
                .setHeader("Cleared in lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(SessionDetailsRenderer.createPenaltyServedRenderer())
                .setHeader("Cleared")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true);

        grid.setItems(penalties);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        return grid;
    }

    private Component createStatisticsLayout(String fileChecksum, int carId) {
        VerticalLayout layout = new VerticalLayout();

        DownloadHandler downloadHandler = (event) -> {
            event.setFileName(String.format("session_laps_%s_%s.csv", fileChecksum, carId));

            String csv = this.exportLapsCSV();
            if (csv != null) {
                event.getOutputStream().write(csv.getBytes(StandardCharsets.UTF_8));
            } else {
                event.getResponse().setStatus(404);
            }
        };

        Anchor downloadLapsAnchor = sessionComponentFactory.createDownloadAnchor(downloadHandler, "Download laps");

        layout.add(downloadLapsAnchor);
        return layout;
    }

    private String exportLapsCSV() {
        if (laps == null) {
            return null;
        }

        try (StringWriter writer = new StringWriter()) {
            StatefulBeanToCsv<Lap> sbc = new StatefulBeanToCsvBuilder<Lap>(writer)
                    .withSeparator(';')
                    .build();
            sbc.write(laps);

            return writer.toString();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            log.severe("An error occurred during creation of CSV resource: " + e.getMessage());
            return null;
        }
    }

    private Tab createTab(String label) {
        return createTab(label, null);
    }

    private Tab createTab(String label, Integer number) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.getStyle()
                .set("gap", "var(--lumo-space-s)");

        Span labelSpan = new Span(label);
        horizontalLayout.add(labelSpan);

        if (number != null) {
            Span numberSpan = new Span(String.valueOf(number));
            numberSpan.getElement().getThemeList().add("badge contrast");
            horizontalLayout.add(numberSpan);
        }

        return new Tab(horizontalLayout);
    }
}
