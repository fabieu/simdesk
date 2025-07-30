package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.streams.DownloadHandler;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.comparator.BopComparator;
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
@Profile(ProfileManager.PROFILE_BOP)
@Route(value = "/bop/overview")
@PageTitle("Balance of Performance - Overview")
@AnonymousAllowed
public class BopDisplayView extends BaseView implements BeforeEnterObserver {
    private final BopService bopService;
    private final NotificationService notificationService;

    private final ComponentFactory componentFactory;

    private final JsonClient jsonClient;

    private RouteParameters routeParameters;
    private QueryParameters queryParameters;

    private final Select<String> trackSelect = new Select<>();
    private final Map<String, Component> scrollTargets = new LinkedHashMap<>();

    public BopDisplayView(BopService bopService,
                          NotificationService notificationService,
                          ComponentFactory componentFactory,
                          JsonClient jsonClient) {
        this.bopService = bopService;
        this.notificationService = notificationService;
        this.componentFactory = componentFactory;
        this.jsonClient = jsonClient;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        removeAll();

        add(createViewHeader());
        add(createTrackSelectionLayout());
        addAndExpand(createBopGrid());

        Optional<String> trackIdParameter = queryParameters.getSingleParameter(QUERY_PARAMETER_TRACK_ID);
        if (trackIdParameter.isPresent() && Track.existsInAcc(trackIdParameter.get())) {
            Optional.ofNullable(scrollTargets.get(trackIdParameter.get())).ifPresent(this::scrollToComponent);
        }
    }

    private Component createTrackSelectionLayout() {
        // Track selection
        HorizontalLayout trackSelectionLayout = new HorizontalLayout();
        trackSelectionLayout.setWidthFull();
        trackSelectionLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        trackSelectionLayout.getStyle()
                .setPadding("0 var(--lumo-space-m)");

        trackSelect.setWidthFull();
        trackSelect.setPlaceholder("Jump to track");
        trackSelect.setItemLabelGenerator(Track::getTrackNameByAccId);
        trackSelect.addValueChangeListener(event -> {
            String trackId = event.getValue();
            if (trackId != null) {
                Optional.ofNullable(scrollTargets.get(trackId)).ifPresent(component -> {
                    updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TRACK_ID, trackId));
                    scrollToComponent(component);
                });
            }
        });
        trackSelectionLayout.add(trackSelect);

        return trackSelectionLayout;
    }

    private Component createBopGrid() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);

        Map<String, Set<Bop>> bopsByTrack = bopService.getActive().stream()
                .sorted(new BopComparator())
                .collect(Collectors.groupingBy(Bop::getTrackId, TreeMap::new, Collectors.toCollection(LinkedHashSet::new)));

        for (Map.Entry<String, Set<Bop>> entry : bopsByTrack.entrySet()) {
            String trackId = entry.getKey();
            VerticalLayout trackLayout = new VerticalLayout();
            trackLayout.setPadding(false);

            DownloadHandler downloadHandler = (event) -> {
                List<AccBopEntry> accBopEntries = entry.getValue().stream()
                        .map(bopService::convertToAccBopEntry)
                        .toList();
                String json = jsonClient.toJson(new AccBop(accBopEntries));

                event.setFileName(String.format("bop_%s_%s.json", entry.getKey(), FormatUtils.formatDatetimeSafe(Instant.now())));
                event.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            };

            // Header
            H2 trackTitle = new H2(Track.getTrackNameByAccId(trackId));
            scrollTargets.put(trackId, trackTitle);

            Anchor downloadAnchor = new Anchor(downloadHandler, "");
            downloadAnchor.getElement().setAttribute("download", true);
            downloadAnchor.removeAll();
            Button downloadButton = new Button(componentFactory.getDownloadIcon());
            downloadButton.setTooltipText("Download");
            downloadAnchor.add(downloadButton);

            Button shareButton = new Button(componentFactory.getShareIcon());
            shareButton.setTooltipText("Share");
            shareButton.addClickListener(event -> {
                        Page page = UI.getCurrent().getPage();
                        page.fetchCurrentURL(url -> {
                            String shareUrl = UriComponentsBuilder.fromUriString(url.toString())
                                    .replaceQueryParam(QUERY_PARAMETER_TRACK_ID, entry.getKey())
                                    .toUriString();
                            page.executeJs(String.format("navigator.clipboard.writeText('%s')", shareUrl));
                            notificationService.showSuccessNotification("Copied share link to clipboard");
                        });
                    }
            );

            HorizontalLayout header = new HorizontalLayout();
            header.setAlignItems(FlexComponent.Alignment.CENTER);
            header.add(trackTitle, downloadAnchor, shareButton);

            // Grid
            Grid<Bop> grid = new Grid<>(Bop.class, false);
            grid.setItems(entry.getValue());
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

            trackLayout.add(header, grid, ComponentUtils.createSpacer());
            layout.add(trackLayout);
        }

        trackSelect.setItems(new ArrayList<>(scrollTargets.keySet()));

        return layout;
    }
}
