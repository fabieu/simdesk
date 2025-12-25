package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.streams.DownloadHandler;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.RaceTrack;
import de.sustineo.simdesk.entities.RaceTracks;
import de.sustineo.simdesk.entities.Simulation;
import de.sustineo.simdesk.entities.bop.Bop;
import de.sustineo.simdesk.entities.bop.BopProvider;
import de.sustineo.simdesk.entities.comparator.AccBopComparator;
import de.sustineo.simdesk.entities.json.kunos.acc.AccBop;
import de.sustineo.simdesk.entities.json.kunos.acc.AccBopEntry;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.bop.BopService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.utils.json.JsonClient;
import de.sustineo.simdesk.views.components.ComponentFactory;
import de.sustineo.simdesk.views.generators.BopCarGroupPartNameGenerator;
import de.sustineo.simdesk.views.generators.InactiveBopPartNameGenerator;
import de.sustineo.simdesk.views.renderers.BopRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Profile(SpringProfile.BOP)
@Route(value = "/bop/overview")
@AnonymousAllowed
public class BopDisplayView extends BaseView {
    private final BopService bopService;
    private final NotificationService notificationService;
    private final ComponentFactory componentFactory;

    private final TabSheet tabSheet = new TabSheet();
    private final Map<String, Tab> tabsByTrackId = new LinkedHashMap<>();

    public BopDisplayView(BopService bopService,
                          NotificationService notificationService,
                          ComponentFactory componentFactory) {
        this.bopService = bopService;
        this.notificationService = notificationService;
        this.componentFactory = componentFactory;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public String getPageTitle() {
        return "Balance of Performance - Overview";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        removeAll();

        add(createViewHeader());
        addAndExpand(createContainer());

        Optional<String> simulationIdParameter = queryParameters.getSingleParameter(QUERY_PARAMETER_SIMULATION_ID);
        Optional<String> trackIdParameter = queryParameters.getSingleParameter(QUERY_PARAMETER_TRACK_ID);
        if (simulationIdParameter.isPresent() && trackIdParameter.isPresent() && RaceTracks.exists(Simulation.getById(simulationIdParameter.get()), trackIdParameter.get())) {
            tabSheet.setSelectedTab(tabsByTrackId.get(trackIdParameter.get()));
        }
    }

    private Component createContainer() {
        Div layout = new Div();
        layout.addClassNames("container", "bg-light");
        layout.add(createDisclaimerLayout(), createTabSheetLayout());
        return layout;
    }

    private Component createDisclaimerLayout() {
        Set<BopProvider> bopProviders = bopService.getBopProviders();
        if (bopProviders == null || bopProviders.isEmpty()) {
            return new Div(); // Empty component
        }

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);

        List<Anchor> anchors = bopProviders.stream()
                .sorted()
                .map(bopProvider -> new Anchor(bopProvider.getReference(), bopProvider.getName(), AnchorTarget.BLANK))
                .toList();

        Span disclaimerSpan = new Span();

        Span title = new Span("Disclaimer: ");
        title.getStyle()
                .setFontWeight(Style.FontWeight.BOLD);
        disclaimerSpan.add(title);

        disclaimerSpan.add("The values presented may incorporate Balance of Performance (BoP) data provided by ");

        for (int i = 0; i < anchors.size(); i++) {
            disclaimerSpan.add(anchors.get(i));

            if (i < anchors.size() - 2) {
                disclaimerSpan.add(new Text(", "));
            } else if (i == anchors.size() - 2) {
                disclaimerSpan.add(new Text(" and "));
            }
        }

        layout.add(disclaimerSpan);
        return layout;
    }

    private Component createTabSheetLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        tabSheet.setWidthFull();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);
        tabSheet.addSelectedChangeListener(event -> {
            for (Map.Entry<String, Tab> entry : tabsByTrackId.entrySet()) {
                if (entry.getValue() == event.getSelectedTab()) {
                    updateQueryParameters(routeParameters, QueryParameters.simple(Map.of(
                            QUERY_PARAMETER_SIMULATION_ID, Simulation.ACC.getId(),
                            QUERY_PARAMETER_TRACK_ID, entry.getKey()
                    )));
                }
            }
        });

        Map<RaceTrack, Set<Bop>> bopsByRaceTrack = bopService.getActive().stream()
                .sorted(new AccBopComparator())
                .collect(Collectors.groupingBy(bop -> RaceTracks.getById(Simulation.ACC, bop.getTrackId()), LinkedHashMap::new, Collectors.toCollection(LinkedHashSet::new)));

        for (Map.Entry<RaceTrack, Set<Bop>> entry : bopsByRaceTrack.entrySet()) {
            RaceTrack raceTrack = entry.getKey();
            Set<Bop> bopEntries = entry.getValue();

            VerticalLayout tabLayout = new VerticalLayout();

            DownloadHandler downloadHandler = (event) -> {
                List<AccBopEntry> accBopEntries = bopEntries.stream()
                        .map(bopService::convertToAccBopEntry)
                        .toList();
                String json = JsonClient.toJson(new AccBop(accBopEntries));

                event.setFileName(String.format("bop_%s_%s.json", raceTrack.getGlobalId(), FormatUtils.formatDatetimeSafe(Instant.now())));
                event.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            };

            // Header
            Anchor downloadAnchor = new Anchor(downloadHandler, "");
            downloadAnchor.removeAll();
            Button downloadButton = new Button("Download", componentFactory.getDownloadIcon());
            downloadButton.setTooltipText("Download");
            downloadAnchor.add(downloadButton);

            Button shareButton = new Button("Share", componentFactory.getShareIcon());
            shareButton.setTooltipText("Share");
            shareButton.addClickListener(event -> {
                        Page page = UI.getCurrent().getPage();
                        page.fetchCurrentURL(url -> {
                            String shareUrl = UriComponentsBuilder.fromUriString(url.toString())
                                    .replaceQueryParam(QUERY_PARAMETER_TRACK_ID, raceTrack.getId(Simulation.ACC))
                                    .replaceQueryParam(QUERY_PARAMETER_SIMULATION_ID, Simulation.ACC.getId())
                                    .toUriString();
                            page.executeJs(String.format("navigator.clipboard.writeText('%s')", shareUrl));
                            notificationService.showSuccessNotification("Copied share link to clipboard");
                        });
                    }
            );

            HorizontalLayout headerLayout = new HorizontalLayout();
            headerLayout.setWidthFull();
            headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            headerLayout.setJustifyContentMode(JustifyContentMode.END);
            headerLayout.add(downloadAnchor, shareButton);

            // Grid
            Grid<Bop> grid = new Grid<>(Bop.class, false);
            grid.setItems(bopEntries);
            grid.setAllRowsVisible(true);
            grid.setWidthFull();
            grid.setPartNameGenerator(new InactiveBopPartNameGenerator());
            grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER);
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.setPartNameGenerator(new BopCarGroupPartNameGenerator());

            grid.addColumn(bop -> AccCar.getModelById(bop.getCarId()))
                    .setHeader("Car")
                    .setSortable(true);
            grid.addColumn(BopRenderer.createRestrictorRenderer())
                    .setHeader("Restrictor")
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.END)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(Bop::getRestrictor);
            grid.addColumn(BopRenderer.createBallastKgRenderer())
                    .setHeader("Ballast")
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.END)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(Bop::getBallastKg);
            grid.addColumn(bop -> FormatUtils.formatDate(bop.getUpdateDatetime()))
                    .setHeader("Last change")
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(Bop::getUpdateDatetime);

            tabLayout.add(headerLayout, grid);

            Tab tab = new Tab(raceTrack.getDisplayName());
            tabsByTrackId.put(raceTrack.getId(Simulation.ACC), tab);
            tabSheet.add(tab, tabLayout);
        }

        layout.add(tabSheet);
        return layout;
    }
}
