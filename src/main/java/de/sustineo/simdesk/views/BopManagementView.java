package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveListener;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.comparator.BopComparator;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.bop.BopService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.filter.BopManagementFilter;
import de.sustineo.simdesk.views.filter.GridFilter;
import de.sustineo.simdesk.views.generators.BopCarGroupPartNameGenerator;
import de.sustineo.simdesk.views.renderers.BopRenderer;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
@Profile(ProfileManager.PROFILE_BOP)
@Route(value = "/bop/management")
@PageTitle("Balance of Performance - Management")
@RolesAllowed({"ADMIN"})
public class BopManagementView extends BaseView {
    private final static String GRID_FILTER_TRACK = "TRACK";
    private final static String GRID_FILTER_CAR_GROUP = "CAR_GROUP";
    private final static String GRID_FILTER_ACTIVE = "ACTIVE";

    private final BopService bopService;
    private final SecurityService securityService;
    private final NotificationService notificationService;

    private final List<Bop> bopList = new ArrayList<>();
    private final Grid<Bop> grid = new Grid<>(Bop.class, false);
    private final GridListDataView<Bop> gridDataView = grid.setItems(bopList);
    private final Map<String, SerializablePredicate<Bop>> gridFilters = new HashMap<>();

    public BopManagementView(BopService bopService,
                             SecurityService securityService,
                             NotificationService notificationService) {
        this.bopService = bopService;
        this.securityService = securityService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        add(createActionsLayout());
        addAndExpand(createBopGrid());
        add(createFooter());

        initializeBopList();
    }

    private void initializeBopList() {
        List<Bop> sortedBops = bopService.getAll().stream()
                .sorted(new BopComparator())
                .toList();
        this.bopList.addAll(sortedBops);

        gridDataView.refreshAll();
    }

    private void refreshFilters() {
        SerializablePredicate<Bop> filters = gridFilters.values().stream()
                .reduce(SerializablePredicate::and)
                .orElse(null);
        gridDataView.setFilter(filters);
    }

    private Component createActionsLayout() {
        ComboBox<Track> trackComboBox = new ComboBox<>("Track");

        Button enableTrackButton = new Button("Enable");
        enableTrackButton.setEnabled(false);
        enableTrackButton.addClickListener(e -> {
            Track track = trackComboBox.getValue();
            if (track != null) {
                enableAllForTrack(track.getTrackId());
                notificationService.showSuccessNotification("All BOPs for track " + track.getTrackName() + " have been enabled");
            }
        });

        Button disableTrackButton = new Button("Disable");
        disableTrackButton.setEnabled(false);
        disableTrackButton.addClickListener(e -> {
            Track track = trackComboBox.getValue();
            if (track != null) {
                disableAllForTrack(track.getTrackId());
                notificationService.showSuccessNotification("All BOPs for track " + track.getTrackName() + " have been disabled");
            }
        });

        Button resetAllForTrackButton = new Button("Reset");
        resetAllForTrackButton.setEnabled(false);
        resetAllForTrackButton.addClickListener(e -> {
            Track track = trackComboBox.getValue();
            if (track != null) {
                resetAllForTrack(track.getTrackId());
                notificationService.showSuccessNotification("All BOPs for track " + track.getTrackName() + " have been reset");
            }
        });

        trackComboBox.setItems(Track.getAllSortedByName());
        trackComboBox.setItemLabelGenerator(Track::getTrackName);
        trackComboBox.setClearButtonVisible(true);
        trackComboBox.setPlaceholder("Select track");
        trackComboBox.addValueChangeListener(e -> {
            Track track = e.getValue();
            if (track != null) {
                enableTrackButton.setEnabled(true);
                disableTrackButton.setEnabled(true);
                resetAllForTrackButton.setEnabled(true);
            } else {
                enableTrackButton.setEnabled(false);
                disableTrackButton.setEnabled(false);
                resetAllForTrackButton.setEnabled(false);
            }
        });

        ComboBox<Track> trackFilterComboxBox = new ComboBox<>();
        trackFilterComboxBox.setLabel("Track");
        trackFilterComboxBox.setItems(Track.getAllSortedByName());
        trackFilterComboxBox.setItemLabelGenerator(Track::getTrackName);
        trackFilterComboxBox.setClearButtonVisible(true);
        trackFilterComboxBox.addValueChangeListener(e -> {
            Track track = e.getValue();
            if (track != null) {
                gridFilters.put(GRID_FILTER_TRACK, bop -> track.getTrackId().equals(bop.getTrackId()));
            } else {
                gridFilters.remove(GRID_FILTER_TRACK);
            }
            refreshFilters();
        });

        ComboBox<CarGroup> carGroupFilterComboBox = new ComboBox<>();
        carGroupFilterComboBox.setLabel("Car Group");
        carGroupFilterComboBox.setItems(CarGroup.values());
        carGroupFilterComboBox.setItemLabelGenerator(CarGroup::name);
        carGroupFilterComboBox.setClearButtonVisible(true);
        carGroupFilterComboBox.addValueChangeListener(e -> {
            CarGroup carGroup = e.getValue();
            if (carGroup != null) {
                gridFilters.put(GRID_FILTER_CAR_GROUP, bop -> carGroup.equals(Car.getCarGroupById(bop.getCarId())));
            } else {
                gridFilters.remove(GRID_FILTER_CAR_GROUP);
            }
            refreshFilters();
        });

        ComboBox<Boolean> activeFilterComboBox = new ComboBox<>();
        activeFilterComboBox.setLabel("Active");
        activeFilterComboBox.setItems(true, false);
        activeFilterComboBox.setClearButtonVisible(true);
        activeFilterComboBox.addValueChangeListener(e -> {
            Boolean active = e.getValue();
            if (active != null) {
                gridFilters.put(GRID_FILTER_ACTIVE, bop -> active.equals(bop.getActive()));
            } else {
                gridFilters.remove(GRID_FILTER_ACTIVE);
            }
            refreshFilters();
        });

        HorizontalLayout trackActionLayout = new HorizontalLayout(enableTrackButton, disableTrackButton, resetAllForTrackButton);
        trackActionLayout.getStyle()
                .setMarginRight("auto");

        FlexLayout bopActionLayout = new FlexLayout(trackComboBox, trackActionLayout, trackFilterComboxBox, carGroupFilterComboBox, activeFilterComboBox);
        bopActionLayout.setWidthFull();
        bopActionLayout.setAlignItems(Alignment.END);
        bopActionLayout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-m)");

        VerticalLayout layout = new VerticalLayout();
        layout.add(bopActionLayout);

        return layout;
    }

    private Component createBopGrid() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        Editor<Bop> editor = grid.getEditor();
        grid.setSizeFull();
        grid.setMultiSort(true, true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new BopCarGroupPartNameGenerator());

        Binder<Bop> binder = new Binder<>(Bop.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener((EditorSaveListener<Bop>) event -> {
            Bop bop = event.getItem();
            bop.setUsername(securityService.getAuthenticatedUsername());
            bop.setUpdateDatetime(Instant.now());
            bopService.update(bop);
        });

        Grid.Column<Bop> trackNameColumn = grid.addColumn(bop -> Track.getTrackNameById(bop.getTrackId()))
                .setHeader("Track")
                .setSortable(true);
        Grid.Column<Bop> carNameColumn = grid.addColumn(bop -> Car.getCarNameById(bop.getCarId()))
                .setHeader("Car")
                .setSortable(true);
        Grid.Column<Bop> restrictorColumn = grid.addColumn(BopRenderer.createRestrictorRenderer())
                .setHeader("Restrictor")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Bop::getRestrictor);
        Grid.Column<Bop> ballastKgColumn = grid.addColumn(BopRenderer.createBallastKgRenderer())
                .setHeader("Ballast (kg)")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Bop::getBallastKg);
        Grid.Column<Bop> activeColumn = grid.addColumn(Bop::getActive)
                .setHeader("Active")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Bop> usernameColumn = grid.addColumn(Bop::getUsername)
                .setHeader("Changed by")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Bop> updateDatetimeColumn = grid.addColumn(bop -> FormatUtils.formatDatetime(bop.getUpdateDatetime()))
                .setHeader("Last change")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Bop::getUpdateDatetime);
        Grid.Column<Bop> editColumn = grid.addComponentColumn(bop -> {
                    Button editButton = new Button("Edit");
                    editButton.addClickListener(e -> {
                        if (editor.isOpen()) {
                            editor.cancel();
                        }
                        editor.editItem(bop);
                    });
                    return editButton;
                })
                .setWidth("150px")
                .setFlexGrow(0);

        IntegerField ballastKgField = new IntegerField();
        ballastKgField.setWidthFull();
        binder.forField(ballastKgField)
                .asRequired("Ballast must not be empty")
                .withValidator(value -> value >= -40 && value <= 40, "Ballast must be between -40 and 40kg")
                .bind(Bop::getBallastKg, Bop::setBallastKg);
        ballastKgColumn.setEditorComponent(ballastKgField);

        IntegerField restrictorField = new IntegerField();
        restrictorField.setWidthFull();
        binder.forField(restrictorField)
                .asRequired("Restrictor must not be empty")
                .withValidator(value -> value >= 0 && value <= 20, "Value must be between 0 and 20%")
                .bind(Bop::getRestrictor, Bop::setRestrictor);
        restrictorColumn.setEditorComponent(restrictorField);

        Select<Boolean> activeField = new Select<>();
        activeField.setItems(true, false);
        activeField.setWidthFull();
        binder.forField(activeField)
                .asRequired()
                .bind(Bop::getActive, Bop::setActive);
        activeColumn.setEditorComponent(activeField);

        Button saveButton = new Button("Save", e -> editor.save());
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        BopManagementFilter filter = new BopManagementFilter(gridDataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(trackNameColumn).setComponent(GridFilter.createHeader(filter::setTrackName));
        headerRow.getCell(carNameColumn).setComponent(GridFilter.createHeader(filter::setCarName));
        headerRow.getCell(activeColumn).setComponent(GridFilter.createHeader(filter::setActive));
        headerRow.getCell(usernameColumn).setComponent(GridFilter.createHeader(filter::setUsername));

        layout.add(grid);
        return layout;
    }

    private void enableAllForTrack(@Nullable String trackId) {
        if (trackId == null) {
            return;
        }

        for (Bop bop : bopList) {
            if (!trackId.equals(bop.getTrackId())) {
                continue;
            }

            bop.setActive(true);
            bop.setUsername(securityService.getAuthenticatedUsername());
            bop.setUpdateDatetime(Instant.now());

            bopService.update(bop);
            gridDataView.refreshItem(bop);
        }
    }

    private void disableAllForTrack(@Nullable String trackId) {
        if (trackId == null) {
            return;
        }

        for (Bop bop : bopList) {
            if (!trackId.equals(bop.getTrackId())) {
                continue;
            }

            bop.setActive(false);
            bop.setUsername(securityService.getAuthenticatedUsername());
            bop.setUpdateDatetime(Instant.now());

            bopService.update(bop);
            gridDataView.refreshItem(bop);
        }
    }

    private void resetAllForTrack(@Nullable String trackId) {
        if (trackId == null) {
            return;
        }

        for (Bop bop : bopList) {
            if (!trackId.equals(bop.getTrackId())) {
                continue;
            }

            bop.setRestrictor(0);
            bop.setBallastKg(0);
            bop.setUsername(securityService.getAuthenticatedUsername());
            bop.setUpdateDatetime(Instant.now());

            bopService.update(bop);
            gridDataView.refreshItem(bop);
        }
    }
}
