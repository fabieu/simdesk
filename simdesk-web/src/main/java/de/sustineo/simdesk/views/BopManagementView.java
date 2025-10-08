package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveListener;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.bop.Bop;
import de.sustineo.simdesk.entities.bop.BopProvider;
import de.sustineo.simdesk.entities.comparator.BopComparator;
import de.sustineo.simdesk.entities.json.kunos.acc.AccBop;
import de.sustineo.simdesk.entities.json.kunos.acc.AccBopEntry;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.ValidationService;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.bop.BopService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.utils.json.JsonClient;
import de.sustineo.simdesk.views.components.ButtonComponentFactory;
import de.sustineo.simdesk.views.filter.grid.BopManagementFilter;
import de.sustineo.simdesk.views.filter.grid.GridFilter;
import de.sustineo.simdesk.views.generators.BopCarGroupPartNameGenerator;
import de.sustineo.simdesk.views.i18n.UploadI18NDefaults;
import de.sustineo.simdesk.views.renderers.BopRenderer;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;

@Log
@Profile(ProfileManager.PROFILE_BOP)
@Route(value = "/bop/management")
@RolesAllowed({"ADMIN"})
public class BopManagementView extends BaseView {
    private final static String GRID_FILTER_TRACK = "TRACK";
    private final static String GRID_FILTER_CAR_GROUP = "CAR_GROUP";
    private final static String GRID_FILTER_ACTIVE = "ACTIVE";

    private final BopService bopService;
    private final ValidationService validationService;
    private final NotificationService notificationService;

    private final ButtonComponentFactory buttonComponentFactory;

    private final List<Bop> bopList = new ArrayList<>();
    private final Grid<Bop> grid = new Grid<>(Bop.class, false);
    private final GridListDataView<Bop> gridDataView = grid.setItems(bopList);
    private final Map<String, SerializablePredicate<Bop>> gridFilters = new HashMap<>();
    private final String authenticatedUserGlobalName;

    public BopManagementView(BopService bopService,
                             ValidationService validationService,
                             SecurityService securityService,
                             NotificationService notificationService,
                             ButtonComponentFactory buttonComponentFactory) {
        this.bopService = bopService;
        this.validationService = validationService;
        this.notificationService = notificationService;
        this.buttonComponentFactory = buttonComponentFactory;
        this.authenticatedUserGlobalName = securityService.getAuthenticatedUser()
                .map(user -> user.getGlobalName().orElse(user.getUsername()))
                .orElse(null);

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        add(createActionsLayout());
        addAndExpand(createBopGrid());

        initializeBopList();
    }

    @Override
    public String getPageTitle() {
        return "Balance of Performance - Management";
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
        ComboBox<Track> trackFilterComboxBox = new ComboBox<>();
        trackFilterComboxBox.setItems(Track.getAllOfAccSortedByName());
        trackFilterComboxBox.setItemLabelGenerator(Track::getName);
        trackFilterComboxBox.setPlaceholder("Select track");
        trackFilterComboxBox.setClearButtonVisible(true);
        trackFilterComboxBox.setMinWidth("300px");
        trackFilterComboxBox.addValueChangeListener(e -> {
            Track track = e.getValue();
            if (track != null) {
                gridFilters.put(GRID_FILTER_TRACK, bop -> track.getAccId().equals(bop.getTrackId()));
            } else {
                gridFilters.remove(GRID_FILTER_TRACK);
            }
            refreshFilters();
        });

        ComboBox<CarGroup> carGroupFilterComboBox = new ComboBox<>();
        carGroupFilterComboBox.setItems(CarGroup.getValid());
        carGroupFilterComboBox.setItemLabelGenerator(CarGroup::name);
        carGroupFilterComboBox.setPlaceholder("Select car group");
        carGroupFilterComboBox.setClearButtonVisible(true);
        carGroupFilterComboBox.addValueChangeListener(e -> {
            CarGroup carGroup = e.getValue();
            if (carGroup != null) {
                gridFilters.put(GRID_FILTER_CAR_GROUP, bop -> carGroup.equals(AccCar.getGroupById(bop.getCarId())));
            } else {
                gridFilters.remove(GRID_FILTER_CAR_GROUP);
            }
            refreshFilters();
        });

        ComboBox<Boolean> activeFilterComboBox = new ComboBox<>();
        activeFilterComboBox.setItems(true, false);
        activeFilterComboBox.setPlaceholder("Select active state");
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

        Button enableTrackButton = new Button("Enable");
        enableTrackButton.addClickListener(e -> {
            String trackId = Optional.ofNullable(trackFilterComboxBox.getValue())
                    .map(Track::getAccId)
                    .orElse(null);
            CarGroup carGroup = carGroupFilterComboBox.getValue();
            Boolean active = activeFilterComboBox.getValue();

            enableAllByFilter(trackId, carGroup, active);
        });

        Button disableTrackButton = new Button("Disable");
        disableTrackButton.addClickListener(e -> {
            String trackId = Optional.ofNullable(trackFilterComboxBox.getValue())
                    .map(Track::getAccId)
                    .orElse(null);
            CarGroup carGroup = carGroupFilterComboBox.getValue();
            Boolean active = activeFilterComboBox.getValue();

            disableAllByFilter(trackId, carGroup, active);
        });

        Button resetAllForTrackButton = new Button("Reset");
        resetAllForTrackButton.addClickListener(e -> {
            String trackId = Optional.ofNullable(trackFilterComboxBox.getValue())
                    .map(Track::getAccId)
                    .orElse(null);
            CarGroup carGroup = carGroupFilterComboBox.getValue();
            Boolean active = activeFilterComboBox.getValue();

            resetAllByFilter(trackId, carGroup, active);
        });

        MultiSelectComboBox<BopProvider> bopProviderComboBox = new MultiSelectComboBox<>();
        bopProviderComboBox.setItems(BopProvider.values());
        bopProviderComboBox.setItemLabelGenerator(BopProvider::getName);
        bopProviderComboBox.setPlaceholder("Select BoP providers");
        bopProviderComboBox.setValue(bopService.getBopProviders());
        bopProviderComboBox.setClearButtonVisible(true);
        bopProviderComboBox.setAllowCustomValue(true);
        bopProviderComboBox.setMinWidth("250px");
        bopProviderComboBox.addValueChangeListener(e -> {
            bopService.setBopProviders(e.getValue());
        });

        Button bopDisplayViewButton = new Button("Go to overview");
        bopDisplayViewButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(BopDisplayView.class));
        });

        Button importBopButton = new Button("Import BOP");
        importBopButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        importBopButton.addClickListener(e -> createImportBopDialog().open());

        HorizontalLayout navigationLayout = new HorizontalLayout(bopDisplayViewButton, importBopButton, bopProviderComboBox);
        navigationLayout.getStyle()
                .setMarginRight("auto");

        FlexLayout actionLayout = new FlexLayout(navigationLayout, enableTrackButton, disableTrackButton, resetAllForTrackButton, trackFilterComboxBox, carGroupFilterComboBox, activeFilterComboBox);
        actionLayout.setWidthFull();
        actionLayout.setAlignItems(Alignment.END);
        actionLayout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-m)");

        VerticalLayout layout = new VerticalLayout();
        layout.add(actionLayout);

        return layout;
    }

    private Component createBopGrid() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        grid.setSizeFull();
        grid.setMultiSort(true, true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new BopCarGroupPartNameGenerator());

        Editor<Bop> editor = grid.getEditor();
        Binder<Bop> binder = new Binder<>(Bop.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener((EditorSaveListener<Bop>) event -> {
            Bop bop = event.getItem();
            bop.setUsername(authenticatedUserGlobalName);
            bop.setUpdateDatetime(Instant.now());
            bopService.update(bop);
        });

        Grid.Column<Bop> trackNameColumn = grid.addColumn(bop -> Track.getTrackNameByAccId(bop.getTrackId()))
                .setHeader("Track")
                .setSortable(true);
        Grid.Column<Bop> carModelColumn = grid.addColumn(bop -> AccCar.getModelById(bop.getCarId()))
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
                    Button editButton = buttonComponentFactory.createPrimaryButton("Update");
                    editButton.addClickListener(e -> {
                        if (editor.isOpen()) {
                            editor.cancel();
                        }
                        editor.editItem(bop);
                    });
                    return editButton;
                })
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("170px")
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

        Button saveButton = buttonComponentFactory.createPrimarySuccessButton("Save");
        saveButton.addClickListener(e -> editor.save());

        Button cancelButton = buttonComponentFactory.createCancelIconButton();
        cancelButton.addClickListener(e -> editor.cancel());

        HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        BopManagementFilter filter = new BopManagementFilter(gridDataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(trackNameColumn).setComponent(GridFilter.createTextFieldHeader(filter::setTrackName));
        headerRow.getCell(carModelColumn).setComponent(GridFilter.createTextFieldHeader(filter::setCarModel));
        headerRow.getCell(activeColumn).setComponent(GridFilter.createTextFieldHeader(filter::setActive));
        headerRow.getCell(usernameColumn).setComponent(GridFilter.createTextFieldHeader(filter::setUsername));

        layout.add(grid);
        return layout;
    }

    private void enableAllByFilter(@Nullable String trackId, @Nullable CarGroup carGroup, @Nullable Boolean active) {
        if (trackId == null && carGroup == null && active == null) {
            notificationService.showWarningNotification("No filters provided — nothing to reset.");
            return;
        }

        for (Bop bop : bopList) {
            if (trackId != null && !trackId.equals(bop.getTrackId())) {
                continue;
            }

            if (carGroup != null && !carGroup.equals(AccCar.getGroupById(bop.getCarId()))) {
                continue;
            }

            if (active != null && active != bop.getActive()) {
                continue;
            }

            bop.setActive(true);
            bop.setUsername(authenticatedUserGlobalName);
            bop.setUpdateDatetime(Instant.now());

            bopService.update(bop);
            gridDataView.refreshItem(bop);
        }

        refreshFilters();

        String filterDescription = getFilterDescription(trackId, carGroup, active);
        String message = "Enabled BOPs" + (filterDescription.isEmpty() ? "" : " for " + filterDescription);
        notificationService.showSuccessNotification(message);
    }

    private void disableAllByFilter(@Nullable String trackId, @Nullable CarGroup carGroup, @Nullable Boolean active) {
        if (trackId == null && carGroup == null && active == null) {
            notificationService.showWarningNotification("No filters provided — nothing to reset.");
            return;
        }

        for (Bop bop : bopList) {
            if (trackId != null && !trackId.equals(bop.getTrackId())) {
                continue;
            }

            if (carGroup != null && !carGroup.equals(AccCar.getGroupById(bop.getCarId()))) {
                continue;
            }

            if (active != null && active != bop.getActive()) {
                continue;
            }

            bop.setActive(false);
            bop.setUsername(authenticatedUserGlobalName);
            bop.setUpdateDatetime(Instant.now());

            bopService.update(bop);
            gridDataView.refreshItem(bop);
        }

        refreshFilters();

        String filterDescription = getFilterDescription(trackId, carGroup, active);
        String message = "Disabled BOPs" + (filterDescription.isEmpty() ? "" : " for " + filterDescription);
        notificationService.showSuccessNotification(message);
    }

    private void resetAllByFilter(@Nullable String trackId, @Nullable CarGroup carGroup, @Nullable Boolean active) {
        if (trackId == null && carGroup == null && active == null) {
            notificationService.showWarningNotification("No filters provided — nothing to reset.");
            return;
        }

        for (Bop bop : bopList) {
            if (trackId != null && !trackId.equals(bop.getTrackId())) {
                continue;
            }

            if (carGroup != null && !carGroup.equals(AccCar.getGroupById(bop.getCarId()))) {
                continue;
            }

            if (active != null && active != bop.getActive()) {
                continue;
            }

            bop.setRestrictor(0);
            bop.setBallastKg(0);
            bop.setUsername(authenticatedUserGlobalName);
            bop.setUpdateDatetime(Instant.now());

            bopService.update(bop);
            gridDataView.refreshItem(bop);
        }

        refreshFilters();

        String filterDescription = getFilterDescription(trackId, carGroup, active);
        String message = "Reset BOPs" + (filterDescription.isEmpty() ? "" : " for " + filterDescription);
        notificationService.showSuccessNotification(message);
    }

    private String getFilterDescription(@Nullable String trackId, @Nullable CarGroup carGroup, @Nullable Boolean active) {
        List<String> parts = new ArrayList<>();

        if (trackId != null) {
            String trackName = Track.getTrackNameByAccId(trackId);
            parts.add("track " + trackName);
        }

        if (carGroup != null) {
            parts.add("car group " + carGroup);
        }

        if (active != null) {
            parts.add(active ? "state active" : "state inactive");
        }

        return String.join(" and ", parts);
    }

    private Dialog createImportBopDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Import BOP from bop.json");
        dialog.setResizable(true);
        dialog.setWidth("800px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);

        Paragraph uploadHint = new Paragraph("Upload a bop.json file to import BOP settings. You can select which providers to enable and which tracks to import.");
        uploadHint.getStyle()
                .setFontSize("var(--lumo-font-size-s)")
                .setColor("var(--lumo-secondary-text-color)");

        Upload fileUpload = new Upload();
        fileUpload.setWidthFull();
        fileUpload.setDropAllowed(true);
        fileUpload.setAcceptedFileTypes("application/json", ".json");
        fileUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        fileUpload.setI18n(configureUploadI18N());
        fileUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(event.getErrorMessage()));

        MultiSelectComboBox<BopProvider> providerSelector = new MultiSelectComboBox<>("BOP Providers to enable");
        providerSelector.setItems(BopProvider.values());
        providerSelector.setItemLabelGenerator(BopProvider::getName);
        providerSelector.setWidthFull();
        providerSelector.setHelperText("Select which BOP providers should be enabled after import");

        CheckboxGroup<String> trackSelector = new CheckboxGroup<>();
        trackSelector.setLabel("Select tracks to import");
        trackSelector.setWidthFull();
        trackSelector.setVisible(false);

        Button importButton = buttonComponentFactory.createPrimarySuccessButton("Import");
        importButton.setEnabled(false);

        final AccBop[] uploadedBop = {null};

        fileUpload.setUploadHandler(UploadHandler.inMemory((metadata, data) -> {
            try {
                AccBop bop = JsonClient.fromJson(new String(data), AccBop.class);
                validationService.validate(bop);

                uploadedBop[0] = bop;

                Map<String, String> trackMap = new LinkedHashMap<>();
                for (AccBopEntry entry : bop.getEntries()) {
                    String trackId = entry.getTrackId();
                    Track track = Track.getByAccId(trackId);
                    if (track != null && !trackMap.containsKey(trackId)) {
                        trackMap.put(trackId, track.getName());
                    }
                }

                if (trackMap.isEmpty()) {
                    notificationService.showErrorNotification("No valid tracks found in the uploaded file");
                    trackSelector.setVisible(false);
                    importButton.setEnabled(false);
                    return;
                }

                trackSelector.setItems(trackMap.keySet());
                trackSelector.setItemLabelGenerator(trackMap::get);
                trackSelector.select(trackMap.keySet());
                trackSelector.setVisible(true);
                importButton.setEnabled(true);

                notificationService.showSuccessNotification("File uploaded successfully. Select tracks to import.");
            } catch (Exception e) {
                notificationService.showErrorNotification(metadata.fileName() + " - " + e.getLocalizedMessage());
                trackSelector.setVisible(false);
                importButton.setEnabled(false);
                uploadedBop[0] = null;
            }
        }));

        importButton.addClickListener(event -> {
            if (uploadedBop[0] == null) {
                notificationService.showErrorNotification("Please upload a file first");
                return;
            }

            Set<String> selectedTracks = trackSelector.getSelectedItems();
            if (selectedTracks.isEmpty()) {
                notificationService.showWarningNotification("Please select at least one track to import");
                return;
            }

            Set<BopProvider> selectedProviders = providerSelector.getSelectedItems();

            int importedCount = 0;
            for (AccBopEntry entry : uploadedBop[0].getEntries()) {
                if (selectedTracks.contains(entry.getTrackId())) {
                    Bop existingBop = bopList.stream()
                            .filter(b -> b.getTrackId().equals(entry.getTrackId()) && b.getCarId().equals(entry.getCarId()))
                            .findFirst()
                            .orElse(null);

                    if (existingBop != null) {
                        existingBop.setBallastKg(entry.getBallastKg());
                        existingBop.setRestrictor(entry.getRestrictor());
                        existingBop.setActive(true);
                        existingBop.setUsername(authenticatedUserGlobalName);
                        existingBop.setUpdateDatetime(Instant.now());
                        bopService.update(existingBop);
                        gridDataView.refreshItem(existingBop);
                        importedCount++;
                    }
                }
            }

            if (!selectedProviders.isEmpty()) {
                bopService.setBopProviders(selectedProviders);
            }

            refreshFilters();
            dialog.close();
            notificationService.showSuccessNotification("Successfully imported " + importedCount + " BOP entries");
        });

        Button cancelButton = buttonComponentFactory.createDialogCancelButton(dialog);

        dialogLayout.add(uploadHint, fileUpload, providerSelector, trackSelector);
        dialog.add(dialogLayout);
        dialog.getFooter().add(cancelButton, importButton);

        return dialog;
    }

    private UploadI18N configureUploadI18N() {
        UploadI18NDefaults i18n = new UploadI18NDefaults();

        i18n.getAddFiles().setOne("Upload bop.json file...");
        i18n.getAddFiles().setMany("Upload bop.json files...");
        i18n.getDropFiles().setOne("Drop bop.json file here");
        i18n.getDropFiles().setMany("Drop bop.json files here");
        i18n.getError().setIncorrectFileType("The provided file does not have the correct format (JSON).");
        i18n.getError().setFileIsTooBig("The provided file is too big. Maximum file size is 1 MB.");

        return i18n;
    }
}
