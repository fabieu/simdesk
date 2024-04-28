package de.sustineo.simdesk.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.AccBop;
import de.sustineo.simdesk.entities.json.kunos.AccBopEntry;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.bop.BopService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.utils.json.JsonUtils;
import de.sustineo.simdesk.views.generators.BopCarGroupPartNameGenerator;
import de.sustineo.simdesk.views.generators.InactiveBopPartNameGenerator;
import de.sustineo.simdesk.views.renderers.BopRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_BOP)
@Route(value = "/bop/overview", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Balance of Performance - Overview")
@AnonymousAllowed
public class BopDisplayView extends VerticalLayout {
    private final BopService bopService;
    private final JsonUtils jsonUtils;
    private final ScrollOptions scrollOptions = new ScrollOptions(ScrollOptions.Behavior.SMOOTH);
    private final List<H2> trackTitles = new ArrayList<>();

    public BopDisplayView(BopService bopService, JsonUtils jsonUtils) {
        this.bopService = bopService;
        this.jsonUtils = jsonUtils;

        setId("bop-display-view");
        setSizeFull();
        setPadding(false);

        addAndExpand(createBopGrid());
    }

    private Component createBopGrid() {
        VerticalLayout layout = new VerticalLayout();

        Map<String, Set<Bop>> bopsByTrack = bopService.getActive().stream()
                .sorted(bopService.getComparator())
                .collect(Collectors.groupingBy(Bop::getTrackId, TreeMap::new, Collectors.toCollection(LinkedHashSet::new)));

        // Disclaimer
        H3 disclaimer = new H3("Disclaimer: We might use data provided by Low Fuel Motorsport (LFM). The data may be subject to change.");
        disclaimer.setWidthFull();
        disclaimer.getStyle()
                .setColor("var(--lumo-secondary-text-color)")
                .setTextAlign(Style.TextAlign.CENTER);
        layout.add(disclaimer);

        // Track selection
        HorizontalLayout trackSelectionLayout = new HorizontalLayout();
        trackSelectionLayout.setWidthFull();
        trackSelectionLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Select<String> trackSelect = new Select<>();
        trackSelect.setWidthFull();
        trackSelect.setPlaceholder("Jump to track");
        trackSelect.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                trackTitles.stream()
                        .filter(h2 -> event.getValue().equals(h2.getText()))
                        .findFirst()
                        .ifPresent(h2 -> h2.scrollIntoView(scrollOptions));
            }
        });
        trackSelectionLayout.add(trackSelect);

        layout.add(trackSelectionLayout, ComponentUtils.createSpacer());

        for (Map.Entry<String, Set<Bop>> entry : bopsByTrack.entrySet()) {
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
                            json = jsonUtils.toJson(new AccBop(accBopEntries));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        return new ByteArrayInputStream(json != null ? json.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                    }
            );

            // Header
            H2 trackTitle = new H2(Track.getTrackNameById(entry.getKey()));
            trackTitles.add(trackTitle);

            Anchor downloadAnchor = new Anchor(bopResource, "");
            downloadAnchor.getElement().setAttribute("download", true);
            downloadAnchor.removeAll();
            downloadAnchor.add(new Button("Download", new Icon(VaadinIcon.CLOUD_DOWNLOAD_O)));

            HorizontalLayout header = new HorizontalLayout();
            header.setAlignItems(FlexComponent.Alignment.CENTER);
            header.add(trackTitle, downloadAnchor);

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

        trackSelect.setItems(trackTitles.stream()
                .map(H2::getText)
                .collect(Collectors.toList()));

        return layout;
    }
}
