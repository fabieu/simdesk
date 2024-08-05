package de.sustineo.simdesk.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.AccBop;
import de.sustineo.simdesk.entities.json.kunos.AccBopEntry;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.bop.BopService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.utils.json.JsonUtils;
import de.sustineo.simdesk.views.generators.BopCarGroupPartNameGenerator;
import de.sustineo.simdesk.views.generators.InactiveBopPartNameGenerator;
import de.sustineo.simdesk.views.renderers.BopRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_BOP)
@Route(value = "/bop/overview", layout = MainLayout.class)
@PageTitle("Balance of Performance - Overview")
@AnonymousAllowed
public class BopDisplayView extends BaseView implements BeforeEnterObserver {
    private final BopService bopService;
    private final NotificationService notificationService;

    private RouteParameters routeParameters;
    private QueryParameters queryParameters;

    private final Select<String> trackSelect = new Select<>();
    private final Map<String, Component> scrollTargets = new LinkedHashMap<>();
    private final ScrollOptions scrollOptions = new ScrollOptions(ScrollOptions.Behavior.SMOOTH);

    public BopDisplayView(BopService bopService, NotificationService notificationService) {
        this.bopService = bopService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        add(createViewHeader());
        add(createTrackSelectionLayout());
        addAndExpand(createBopGrid());
        add(createFooter());

        Optional<String> trackIdParameter = queryParameters.getSingleParameter(QUERY_PARAMETER_TRACK_ID);
        if (trackIdParameter.isPresent() && Track.isValid(trackIdParameter.get())) {
            Optional.ofNullable(scrollTargets.get(trackIdParameter.get())).ifPresent(component -> component.scrollIntoView(scrollOptions));
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
        trackSelect.setItemLabelGenerator(Track::getTrackNameById);
        trackSelect.addValueChangeListener(event -> {
            String trackId = event.getValue();
            if (trackId != null) {
                Optional.ofNullable(scrollTargets.get(trackId)).ifPresent(component -> {
                    updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TRACK_ID, trackId));
                    component.scrollIntoView(scrollOptions);
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
                .sorted(bopService.getComparator())
                .collect(Collectors.groupingBy(Bop::getTrackId, TreeMap::new, Collectors.toCollection(LinkedHashSet::new)));

        for (Map.Entry<String, Set<Bop>> entry : bopsByTrack.entrySet()) {
            String trackId = entry.getKey();
            VerticalLayout trackLayout = new VerticalLayout();
            trackLayout.setPadding(false);

            StreamResource bopResource = new StreamResource(
                    String.format("bop_%s_%s.json", entry.getKey(), FormatUtils.formatDatetimeSafe(Instant.now())),
                    () -> {
                        String json;
                        try {
                            List<AccBopEntry> accBopEntries = entry.getValue().stream()
                                    .map(bopService::convertToAccBopEntry)
                                    .toList();
                            json = JsonUtils.toJson(new AccBop(accBopEntries));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        return new ByteArrayInputStream(json != null ? json.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                    }
            );

            // Header
            H2 trackTitle = new H2(Track.getTrackNameById(trackId));
            scrollTargets.put(trackId, trackTitle);

            Anchor downloadAnchor = new Anchor(bopResource, "");
            downloadAnchor.getElement().setAttribute("download", true);
            downloadAnchor.removeAll();
            Button downloadButton = new Button(getDownloadIcon());
            downloadButton.setTooltipText("Download");
            downloadAnchor.add(downloadButton);

            Button shareButton = new Button(getShareIcon());
            shareButton.setTooltipText("Share");
            shareButton.addClickListener(event -> {
                        Page page = UI.getCurrent().getPage();
                        page.fetchCurrentURL(url -> {
                            String shareUrl = UriComponentsBuilder.fromHttpUrl(url.toString())
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

            grid.addColumn(bop -> Car.getCarNameById(bop.getCarId()))
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

        // Disclaimer
        H3 disclaimer = new H3("Disclaimer: We use data provided by Low Fuel Motorsport (LFM) and pitskill.io (PitBoP). The data is subject to change.");
        disclaimer.setWidthFull();
        disclaimer.getStyle()
                .setColor("var(--lumo-secondary-text-color)")
                .setTextAlign(Style.TextAlign.CENTER);
        layout.add(disclaimer);

        return layout;
    }
}
