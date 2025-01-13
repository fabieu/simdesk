package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.entities.comparator.AccEntrylistEntryDefaultIntegerComparator;
import de.sustineo.simdesk.entities.json.kunos.acc.*;
import de.sustineo.simdesk.entities.validation.ValidationData;
import de.sustineo.simdesk.entities.validation.ValidationError;
import de.sustineo.simdesk.entities.validation.ValidationRule;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.ValidationService;
import de.sustineo.simdesk.services.entrylist.EntrylistService;
import de.sustineo.simdesk.utils.json.JsonUtils;
import de.sustineo.simdesk.views.i18n.UploadI18NDefaults;
import de.sustineo.simdesk.views.renderers.EntrylistRenderer;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Profile(ProfileManager.PROFILE_ENTRYLIST)
@Log
@CssImport("flag-icons/css/flag-icons.min.css")
@Route(value = "/entrylist/editor")
@PageTitle("Entrylist - Editor")
@AnonymousAllowed
public class EntrylistEditorView extends BaseView {
    private static final String WEB_STORAGE_KEY_SORTING_MODE = "vaadin.custom.entrylist.sorting.mode";
    private static final String WEB_STORAGE_KEY_SORTING_DIRECTION = "vaadin.custom.entrylist.sorting.direction";
    private final WebStorage.Storage webStorageType = WebStorage.Storage.SESSION_STORAGE;

    private final EntrylistService entrylistService;
    private final ValidationService validationService;
    private final NotificationService notificationService;

    private AccEntrylist entrylist;
    private EntrylistMetadata entrylistMetadata;

    private final Upload entrylistUpload = new Upload();
    private final Anchor downloadAnchor = new Anchor();
    private final Select<SortingModeEntrylist> sortingModeSelect = new Select<>();
    private final Select<SortingDirection> sortdirectionSelect = new Select<>();
    private final TextArea entrylistPreview = new TextArea();
    private final VerticalLayout entrylistLayout = new VerticalLayout();
    private final VerticalLayout entrylistEntriesLayout = new VerticalLayout();

    private LinkedHashMap<AccEntrylistEntry, Component> entrylistEntriesMap = new LinkedHashMap<>();
    private final Comparator<Map.Entry<AccEntrylistEntry, Component>> noopComparator = Comparator.comparing(entry -> 0);
    private final Comparator<Map.Entry<AccEntrylistEntry, Component>> gridPositionComparator = Comparator.comparing(
            entry -> entry.getKey().getDefaultGridPosition(), Comparator.nullsLast(new AccEntrylistEntryDefaultIntegerComparator())
    );
    private final Comparator<Map.Entry<AccEntrylistEntry, Component>> raceNumberComparator = Comparator.comparing(
            entry -> entry.getKey().getRaceNumber(), Comparator.nullsLast(new AccEntrylistEntryDefaultIntegerComparator())
    );
    private final Comparator<Map.Entry<AccEntrylistEntry, Component>> adminComparator = Comparator.comparing(
            entry -> entry.getKey().getIsServerAdmin(), Comparator.nullsLast(Comparator.reverseOrder())
    );

    private final ConfirmDialog resetDialog = createResetDialog();
    private final Dialog validationDialog = createValidationDialog();
    private final Dialog resultsUploadDialog = createResultsUploadDialog();
    private final Dialog customCarsUploadDialog = createCustomCarsUploadDialog();

    public EntrylistEditorView(EntrylistService entrylistService,
                               ValidationService validationService,
                               NotificationService notificationService) {
        this.entrylistService = entrylistService;
        this.validationService = validationService;
        this.notificationService = notificationService;

        this.entrylistPreview.setWidthFull();
        this.entrylistPreview.setReadOnly(true);

        WebStorage.getItem(webStorageType, WEB_STORAGE_KEY_SORTING_MODE, (value) ->
                this.sortingModeSelect.setValue(EnumUtils.getEnum(SortingModeEntrylist.class, value, SortingModeEntrylist.NONE))
        );

        WebStorage.getItem(webStorageType, WEB_STORAGE_KEY_SORTING_DIRECTION, (value) ->
                this.sortdirectionSelect.setValue(EnumUtils.getEnum(SortingDirection.class, value, SortingDirection.ASC))
        );

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createEntrylistContainer());
        add(createFooter());
    }

    private void resetEntities() {
        entrylist = null;
        entrylistMetadata = null;
        entrylistEntriesMap.clear();
    }

    private void reset() {
        entrylistUpload.clearFileList();
        resetEntities();
        refreshEntrylistEditor();
        refreshEntrylistPreview();
    }

    private Component createTabSheets() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);
        tabSheet.add("Editor", entrylistLayout);
        tabSheet.add("Preview", entrylistPreview);
        return tabSheet;
    }

    private Component createEntrylistContainer() {
        VerticalLayout entrylistContainer = new VerticalLayout();
        entrylistContainer.setSizeFull();
        entrylistContainer.setPadding(false);
        entrylistContainer.add(createPopulateEntrylistLayout(), createEntrylistHeaderLayout(), createTabSheets(), createActionLayout());

        Div entrylistContainerWrapper = new Div(entrylistContainer);
        entrylistContainerWrapper.addClassNames("container", "bg-light");

        return entrylistContainerWrapper;
    }

    private Component createPopulateEntrylistLayout() {
        FlexLayout layout = new FlexLayout(createFileUpload(), createNewEntrylistButton());
        layout.setWidthFull();
        layout.getStyle()
                .setAlignItems(Style.AlignItems.CENTER)
                .set("gap", "var(--lumo-space-m)");

        return layout;
    }

    private Component createNewEntrylistButton() {
        ConfirmDialog createNewEntrylistConfirmDialog = createNewEntrylistConfirmDialog();
        createNewEntrylistConfirmDialog.addConfirmListener(event -> {
            createNewEntrylist(new AccEntrylist(), new EntrylistMetadata());
            entrylistUpload.clearFileList();
        });

        Button createEntrylistButton = new Button("Create new entrylist");
        createEntrylistButton.addClassNames("break-word");
        createEntrylistButton.setWidth("25%");
        createEntrylistButton.setHeightFull();
        createEntrylistButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createEntrylistButton.addClickListener(event -> {
            if (this.entrylist != null) {
                createNewEntrylistConfirmDialog.open();
            } else {
                createNewEntrylist(new AccEntrylist(), new EntrylistMetadata());
                entrylistUpload.clearFileList();
            }
        });
        createEntrylistButton.getStyle()
                .setMargin("0");

        return createEntrylistButton;
    }

    private Component createFileUpload() {
        FlexLayout fileUploadLayout = new FlexLayout();
        fileUploadLayout.getStyle()
                .setFlexGrow("1");

        Button uploadButton = new Button("Upload entrylist.json...");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        entrylistUpload.setReceiver(memoryBuffer);
        entrylistUpload.setUploadButton(uploadButton);
        entrylistUpload.setDropAllowed(true);
        entrylistUpload.setAcceptedFileTypes(MediaType.APPLICATION_JSON_VALUE);
        entrylistUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        entrylistUpload.setI18n(configureUploadI18N("entrylist.json"));
        entrylistUpload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();

            AccEntrylist entrylist = JsonUtils.fromJson(fileData, AccEntrylist.class);
            EntrylistMetadata entrylistMetadata = EntrylistMetadata.builder()
                    .fileName(event.getFileName())
                    .type(event.getMIMEType())
                    .contentLength(event.getContentLength())
                    .build();

            // Validate entrylist file against syntax and semantic rules
            validationService.validate(entrylist);

            if (this.entrylist != null) {
                ConfirmDialog createNewEntrylistConfirmDialog = createNewEntrylistConfirmDialog();
                createNewEntrylistConfirmDialog.addConfirmListener(dialogEvent -> {
                    createNewEntrylist(entrylist, entrylistMetadata);
                    createSuccessNotification(entrylistMetadata.getFileName(), "Entrylist loaded successfully");
                });
                createNewEntrylistConfirmDialog.addCancelListener(dialogEvent -> entrylistUpload.clearFileList());
                createNewEntrylistConfirmDialog.open();
            } else {
                createNewEntrylist(entrylist, entrylistMetadata);
                createSuccessNotification(entrylistMetadata.getFileName(), "Entrylist loaded successfully");
            }
        });
        entrylistUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getErrorMessage()));
        entrylistUpload.addFailedListener(event -> notificationService.showErrorNotification(event.getReason().getMessage()));
        entrylistUpload.addFileRemovedListener(event -> resetDialog.open());
        entrylistUpload.getStyle()
                .setFlexGrow("1");

        return entrylistUpload;
    }

    private UploadI18N configureUploadI18N(String fileName) {
        UploadI18NDefaults i18n = new UploadI18NDefaults();
        i18n.getAddFiles().setOne(String.format("Upload %s...", fileName));
        i18n.getDropFiles().setOne(String.format("Drop %s here", fileName));
        i18n.getError().setIncorrectFileType("The provided file does not have the correct format (.json)");
        i18n.getError().setFileIsTooBig("The provided file is too big. Maximum file size is 1 MB");
        return i18n;
    }

    private Component createActionLayout() {
        FlexLayout buttonLayout = new FlexLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-m)");

        // Download
        Button downloadButton = new Button("Download", getDownloadIcon());
        downloadButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        this.downloadAnchor.getElement().setAttribute("download", true);
        this.downloadAnchor.add(downloadButton);

        // Validation
        Button validateButton = new Button("Validate", getValidateIcon());
        validateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        validateButton.addClickListener(e -> validationDialog.open());

        // Reset
        Button resetButton = new Button("Reset", getResetIcon());
        resetButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        resetButton.addClickListener(e -> resetDialog.open());

        buttonLayout.add(downloadAnchor, validateButton, resetButton);
        return buttonLayout;
    }

    private StreamResource downloadEntrylist(EntrylistMetadata entrylistMetadata) {
        return new StreamResource(
                Objects.requireNonNullElse(entrylistMetadata.getFileName(), "entrylist.json"),
                () -> {
                    if (entrylist == null) {
                        return new ByteArrayInputStream(new byte[0]);
                    }

                    return new ByteArrayInputStream(JsonUtils.toJson(entrylist).getBytes(StandardCharsets.UTF_8));
                }
        );
    }

    private Component createEntrylistHeaderLayout() {
        this.sortingModeSelect.setLabel("Sort mode");
        this.sortingModeSelect.setItems(SortingModeEntrylist.values());
        this.sortingModeSelect.setItemLabelGenerator(SortingModeEntrylist::getLabel);
        this.sortingModeSelect.addValueChangeListener(event -> {
            WebStorage.setItem(webStorageType, WEB_STORAGE_KEY_SORTING_MODE, event.getValue().name());
            refreshEntrylistEntriesFromMap();
        });

        this.sortdirectionSelect.setLabel("Sort direction");
        this.sortdirectionSelect.setItems(SortingDirection.values());
        this.sortdirectionSelect.setItemLabelGenerator(SortingDirection::getLabel);
        this.sortdirectionSelect.addValueChangeListener(event -> {
            WebStorage.setItem(webStorageType, WEB_STORAGE_KEY_SORTING_DIRECTION, event.getValue().name());
            refreshEntrylistEntriesFromMap();
        });

        Button reverseGridButton = new Button("Reverse grid positions", new Icon(VaadinIcon.REFRESH));
        reverseGridButton.addClickListener(event -> reverseEntrylistEntries());

        Button importResultsButton = new Button("Import results", getUploadIcon());
        importResultsButton.addClickListener(event -> resultsUploadDialog.open());

        Button loadDefaultCustomCarsButton = new Button("Load custom cars", new Icon(VaadinIcon.CAR));
        loadDefaultCustomCarsButton.addClickListener(event -> customCarsUploadDialog.open());
        loadDefaultCustomCarsButton.getStyle()
                .setMarginRight("auto");

        FlexLayout entrylistHeaderLayout = new FlexLayout(reverseGridButton, importResultsButton, loadDefaultCustomCarsButton, sortingModeSelect, sortdirectionSelect);
        entrylistHeaderLayout.setWidthFull();
        entrylistHeaderLayout.setAlignItems(Alignment.END);
        entrylistHeaderLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        entrylistHeaderLayout.getStyle()
                .set("gap", "var(--lumo-space-s)");

        return entrylistHeaderLayout;
    }

    private void refreshEntrylistEditor() {
        entrylistLayout.removeAll();
        entrylistEntriesLayout.removeAll();

        if (entrylist == null || entrylist.getEntries() == null) {
            return;
        }

        for (AccEntrylistEntry entry : entrylist.getEntries()) {
            entrylistEntriesMap.put(entry, createEntrylistEntryLayout(entry));
        }
        refreshEntrylistEntriesFromMap();

        entrylistLayout.add(createEntrylistBaseLayout(), entrylistEntriesLayout, createEntrylistFooterLayout());
    }

    private void refreshEntrylistEntriesFromMap() {
        if (entrylistEntriesMap.isEmpty()) {
            entrylistEntriesLayout.removeAll();
            return;
        }

        Comparator<Map.Entry<AccEntrylistEntry, Component>> comparator = switch (getSortingMode()) {
            case GRID_POSITION -> gridPositionComparator.thenComparing(raceNumberComparator);
            case CAR_NUMBER -> raceNumberComparator;
            case ADMIN -> adminComparator.thenComparing(raceNumberComparator);
            default -> noopComparator;
        };

        // Reverse comparator if sorting direction is descending
        if (SortingDirection.DESC.equals(getSortingDirection())) {
            comparator = comparator.reversed();
        }

        LinkedHashMap<AccEntrylistEntry, Component> sortedEntrylistEntryMap = new LinkedHashMap<>();
        entrylistEntriesMap.entrySet().stream()
                .sorted(comparator)
                .forEachOrdered(entry -> sortedEntrylistEntryMap.put(entry.getKey(), entry.getValue()));

        entrylist.setEntries(new ArrayList<>(sortedEntrylistEntryMap.keySet()));
        refreshEntrylistPreview();

        entrylistEntriesLayout.removeAll();
        entrylistEntriesLayout.add(sortedEntrylistEntryMap.values());

        entrylistEntriesMap = sortedEntrylistEntryMap;
    }

    private void refreshEntrylistPreview() {
        if (entrylist == null) {
            entrylistPreview.clear();
            return;
        }

        entrylistPreview.setValue(JsonUtils.toJsonPretty(entrylist));
    }

    private void addEntrylistEntry(AccEntrylistEntry entry) {
        Component entrylistEntryLayout = createEntrylistEntryLayout(entry);
        entrylistEntriesMap.put(entry, entrylistEntryLayout);

        refreshEntrylistEntriesFromMap();
        scrollToComponent(entrylistEntryLayout);
    }

    private void removeEntrylistEntry(AccEntrylistEntry entry) {
        entrylistEntriesMap.remove(entry);

        refreshEntrylistEntriesFromMap();
    }

    private void reverseEntrylistEntries() {
        if (entrylist == null || entrylist.getEntries() == null) {
            notificationService.showErrorNotification("Reverse grid positions failed - Entrylist is missing");
            return;
        }

        entrylistService.reverseGridPositions(entrylist);
        refreshEntrylistEditor();

        notificationService.showSuccessNotification("Grid positions reversed successfully");
    }

    private void updateEntrylistFromResults(AccSession accSession, Optional<Integer> gridStartPosition) {
        if (entrylist == null || entrylist.getEntries() == null) {
            notificationService.showErrorNotification("Results import failed - Entrylist is missing");
            return;
        }

        if (accSession == null || accSession.getSessionResult() == null || accSession.getSessionResult().getLeaderboardLines() == null) {
            notificationService.showErrorNotification("Results import failed - No results found");
            return;
        }

        if (!SessionType.Q.equals(accSession.getSessionType())) {
            notificationService.showWarningNotification("Results import failed - Only qualifying results are currently supported");
            return;
        }

        entrylistService.updateFromResults(entrylist, accSession, gridStartPosition);
        refreshEntrylistEditor();

        notificationService.showSuccessNotification("Entrylist updated successfully");
    }

    private void updateEntrylistFromCustomCars(CustomCar[] customCars) {
        if (entrylist == null || entrylist.getEntries() == null) {
            notificationService.showErrorNotification("Custom cars import failed - Entrylist is missing");
            return;
        }

        if (customCars == null || customCars.length == 0) {
            notificationService.showErrorNotification("Custom cars import failed - No custom cars found");
            return;
        }

        entrylistService.updateFromCustomCars(entrylist, customCars);
        refreshEntrylistEditor();

        notificationService.showSuccessNotification("Custom cars updated successfully");
    }

    private Component createEntrylistFooterLayout() {
        Button addEntrylistEntryButton = new Button("Add entry");
        addEntrylistEntryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEntrylistEntryButton.addClickListener(event -> addEntrylistEntry(new AccEntrylistEntry()));

        HorizontalLayout entrylistActionLayout = new HorizontalLayout(addEntrylistEntryButton);
        entrylistActionLayout.setWidthFull();
        entrylistActionLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        return entrylistActionLayout;
    }

    private Component createEntrylistBaseLayout() {
        VerticalLayout entrylistMainLayout = new VerticalLayout();
        entrylistMainLayout.setPadding(false);

        Checkbox forceEntrylistCheckbox = new Checkbox();
        forceEntrylistCheckbox.setLabel("Force entrylist");
        forceEntrylistCheckbox.setValue(Integer.valueOf(1).equals(entrylist.getForceEntryList()));
        forceEntrylistCheckbox.addValueChangeListener(event -> {
            entrylist.setForceEntryList(forceEntrylistCheckbox.getValue() ? 1 : 0);
            refreshEntrylistPreview();
        });

        entrylistMainLayout.add(forceEntrylistCheckbox);
        return entrylistMainLayout;
    }

    private Component createEntrylistEntryLayout(AccEntrylistEntry entry) {
        VerticalLayout entrylistEntryLayout = new VerticalLayout();
        entrylistEntryLayout.getStyle()
                .setBorder("1px solid var(--lumo-contrast-10pct)")
                .setBorderRadius("var(--lumo-border-radius-m)");

        // Race number
        IntegerField raceNumberField = new IntegerField("Car Number");
        raceNumberField.setValue(Integer.valueOf(AccEntrylistEntry.DEFAULT_RACE_NUMBER).equals(entry.getRaceNumber()) ? null : entry.getRaceNumber());
        raceNumberField.setPrefixComponent(new Span("#"));
        raceNumberField.setMin(1);
        raceNumberField.setMax(998);
        raceNumberField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setRaceNumber(AccEntrylistEntry.DEFAULT_RACE_NUMBER);
            } else if (event.getValue() >= 1 && event.getValue() <= 998) {
                entry.setRaceNumber(event.getValue());
            }

            if (SortingModeEntrylist.CAR_NUMBER.equals(getSortingMode())) {
                refreshEntrylistEntriesFromMap();
                scrollToComponent(entrylistEntryLayout);
            } else {
                refreshEntrylistPreview();
            }
        });

        // Ballast
        IntegerField ballastField = new IntegerField("Ballast");
        ballastField.setValue(entry.getBallastKg());
        ballastField.setSuffixComponent(new Span("kg"));
        ballastField.setMin(-40);
        ballastField.setMax(40);
        ballastField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setBallastKg(AccEntrylistEntry.DEFAULT_BALLAST_KG);
            } else if (event.getValue() >= -40 && event.getValue() <= 40) {
                entry.setBallastKg(event.getValue());
            }
            refreshEntrylistPreview();
        });

        // Restrictor
        IntegerField restrictorField = new IntegerField("Restrictor");
        restrictorField.setValue(entry.getRestrictor());
        restrictorField.setSuffixComponent(new Span("%"));
        restrictorField.setMin(0);
        restrictorField.setMax(20);
        restrictorField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setRestrictor(AccEntrylistEntry.DEFAULT_RESTRICTOR);
            } else if (event.getValue() >= 0 && event.getValue() <= 20) {
                entry.setRestrictor(event.getValue());
            }
            refreshEntrylistPreview();
        });

        ComboBox<Car> forcedCarModelComboBox = new ComboBox<>("Car Model");
        ComboBox.ItemFilter<Car> carFilter = (car, filterString) -> car.getName().toLowerCase().contains(filterString.toLowerCase()) || car.getGroup().name().equalsIgnoreCase(filterString);
        forcedCarModelComboBox.setItems(carFilter, Car.getAllSortedByName());
        forcedCarModelComboBox.setItemLabelGenerator(Car::getName);
        forcedCarModelComboBox.setClassNameGenerator(car -> car.getGroup().name());
        forcedCarModelComboBox.setValue(Car.getCarById(entry.getForcedCarModel()));
        forcedCarModelComboBox.addValueChangeListener(event -> {
            Integer carId = Optional.of(event)
                    .map(ComboBox.ValueChangeEvent::getValue)
                    .map(Car::getModelId)
                    .orElse(AccEntrylistEntry.DEFAULT_FORCED_CAR_MODEL);

            entry.setForcedCarModel(carId);
            refreshEntrylistPreview();
        });

        Checkbox overrideCarModelForCustomCarCheckbox = new Checkbox("Enabled");
        overrideCarModelForCustomCarCheckbox.setTooltipText("Enable this option to override the car model for the custom car");

        CheckboxGroup<Checkbox> overrideCarModelForCustomCarCheckboxGroup = new CheckboxGroup<>("Override car model for custom car");
        overrideCarModelForCustomCarCheckboxGroup.setTooltipText("Override car model for custom car");
        overrideCarModelForCustomCarCheckboxGroup.setItems(overrideCarModelForCustomCarCheckbox);
        overrideCarModelForCustomCarCheckboxGroup.setItemLabelGenerator(Checkbox::getLabel);
        overrideCarModelForCustomCarCheckboxGroup.addValueChangeListener(event -> {
            entry.setOverrideCarModelForCustomCar(event.getValue().contains(overrideCarModelForCustomCarCheckbox) ? 1 : 0);
            refreshEntrylistPreview();
        });
        if (Integer.valueOf(1).equals(entry.getOverrideCarModelForCustomCar())) {
            overrideCarModelForCustomCarCheckboxGroup.deselectAll();
            overrideCarModelForCustomCarCheckboxGroup.select(overrideCarModelForCustomCarCheckbox);
        }

        TextField customCarField = new TextField("Custom Car");
        customCarField.setValue(Objects.requireNonNullElse(entry.getCustomCar(), ""));
        customCarField.addValueChangeListener(event -> {
            if (event.getValue() == null || event.getValue().isEmpty()) {
                entry.setCustomCar("");
            } else {
                entry.setCustomCar(event.getValue());
            }
            refreshEntrylistPreview();
        });

        IntegerField defaultGridPositionField = new IntegerField("Grid Position");
        defaultGridPositionField.setValue(Integer.valueOf(AccEntrylistEntry.DEFAULT_DEFAULT_GRID_POSITION).equals(entry.getDefaultGridPosition()) ? null : entry.getDefaultGridPosition());
        defaultGridPositionField.setMin(1);
        defaultGridPositionField.setMax(120);
        defaultGridPositionField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setDefaultGridPosition(AccEntrylistEntry.DEFAULT_DEFAULT_GRID_POSITION);
            } else if (event.getValue() >= 1 && event.getValue() <= 120) {
                entry.setDefaultGridPosition(event.getValue());
            }

            if (SortingModeEntrylist.GRID_POSITION.equals(getSortingMode())) {
                refreshEntrylistEntriesFromMap();
                scrollToComponent(entrylistEntryLayout);
            } else {
                refreshEntrylistPreview();
            }
        });

        Checkbox isServerAdminCheckbox = new Checkbox("Server Admin");
        isServerAdminCheckbox.setValue(Integer.valueOf(1).equals(entry.getIsServerAdmin()));
        isServerAdminCheckbox.addValueChangeListener(event -> {
            entry.setIsServerAdmin(isServerAdminCheckbox.getValue() ? 1 : 0);

            // Override background color for server admins
            setBackGroundColorForServerAdmins(entrylistEntryLayout, isServerAdminCheckbox.getValue());

            if (SortingModeEntrylist.ADMIN.equals(getSortingMode())) {
                refreshEntrylistEntriesFromMap();
                scrollToComponent(entrylistEntryLayout);
            } else {
                refreshEntrylistPreview();
            }
        });

        FormLayout entrylistMainFormLayout = new FormLayout();
        entrylistMainFormLayout.add(
                raceNumberField, ballastField, restrictorField,
                forcedCarModelComboBox, overrideCarModelForCustomCarCheckboxGroup, customCarField,
                defaultGridPositionField,
                isServerAdminCheckbox
        );
        entrylistMainFormLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3));
        entrylistMainFormLayout.setColspan(forcedCarModelComboBox, 3);
        entrylistMainFormLayout.setColspan(customCarField, 2);
        entrylistMainFormLayout.setColspan(defaultGridPositionField, 3);

        VerticalLayout entrylistEntryBaseSideLayout = new VerticalLayout(entrylistMainFormLayout, isServerAdminCheckbox);
        entrylistEntryBaseSideLayout.setPadding(false);
        entrylistEntryBaseSideLayout.getStyle()
                .setPaddingRight("var(--lumo-space-m)");

        Div entrylistEntryBaseSideLayoutWrapper = new Div(entrylistEntryBaseSideLayout);
        entrylistEntryBaseSideLayoutWrapper.addClassNames("pure-u-1", "pure-u-md-1-2");

        Checkbox overrideDriverInfoCheckbox = new Checkbox("Override driver info");
        overrideDriverInfoCheckbox.setValue(Integer.valueOf(1).equals(entry.getOverrideDriverInfo()));
        overrideDriverInfoCheckbox.addValueChangeListener(event -> {
            entry.setOverrideDriverInfo(overrideDriverInfoCheckbox.getValue() ? 1 : 0);
            refreshEntrylistPreview();
        });

        VerticalLayout entrylistEntryDriverSideListLayout = new VerticalLayout();
        entrylistEntryDriverSideListLayout.setPadding(false);

        for (AccDriver driver : entry.getDrivers()) {
            entrylistEntryDriverSideListLayout.add(createEntrylistDriverLayout(driver, entry, entrylistEntryDriverSideListLayout));
            setBackGroundColorForServerAdmins(entrylistEntryLayout, Integer.valueOf(1).equals(entry.getIsServerAdmin()));
        }

        Button addDriverButton = new Button("Add driver");
        addDriverButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addDriverButton.addClickListener(event -> {
            List<AccDriver> drivers = entry.getDrivers();
            if (drivers.size() < AccEntrylistEntry.MAX_DRIVERS) {
                AccDriver driver = new AccDriver();
                drivers.add(driver);
                refreshEntrylistPreview();

                entrylistEntryDriverSideListLayout.add(createEntrylistDriverLayout(driver, entry, entrylistEntryDriverSideListLayout));
            } else {
                notificationService.showWarningNotification("Maximum number of drivers reached");
            }
        });

        HorizontalLayout entrylistEntryDriverSideActionLayout = new HorizontalLayout(addDriverButton);
        entrylistEntryDriverSideActionLayout.setWidthFull();
        entrylistEntryDriverSideActionLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout entrylistEntryDriverSideLayout = new VerticalLayout(overrideDriverInfoCheckbox, entrylistEntryDriverSideListLayout, entrylistEntryDriverSideActionLayout);
        entrylistEntryDriverSideLayout.setPadding(false);

        Div entrylistEntryDriverSideLayoutWrapper = new Div(entrylistEntryDriverSideLayout);
        entrylistEntryDriverSideLayoutWrapper.addClassNames("pure-u-1", "pure-u-md-1-2");

        Button removeEntrylistEntryButton = new Button(new Icon(VaadinIcon.CLOSE));
        removeEntrylistEntryButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        removeEntrylistEntryButton.setAriaLabel("Remove entry");
        removeEntrylistEntryButton.addClickListener(event -> removeEntrylistEntry(entry));

        Button cloneEntrylistEntryButton = new Button("Clone");
        cloneEntrylistEntryButton.setAriaLabel("Clone entry");
        cloneEntrylistEntryButton.addClickListener(event -> addEntrylistEntry(new AccEntrylistEntry(entry)));

        HorizontalLayout entrylistEntryHeaderLayout = new HorizontalLayout(cloneEntrylistEntryButton, removeEntrylistEntryButton);
        entrylistEntryHeaderLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Div entrylistEntryHeaderLayoutWrapper = new Div(entrylistEntryHeaderLayout);
        entrylistEntryHeaderLayoutWrapper.addClassNames("pure-u-1");

        Div entrylistEntryContainer = new Div(entrylistEntryHeaderLayoutWrapper, entrylistEntryBaseSideLayoutWrapper, entrylistEntryDriverSideLayoutWrapper);
        entrylistEntryContainer.setWidthFull();
        entrylistEntryContainer.addClassNames("pure-g");

        entrylistEntryLayout.add(entrylistEntryContainer);
        return entrylistEntryLayout;
    }

    private Component createEntrylistDriverLayout(AccDriver driver, AccEntrylistEntry entry, VerticalLayout entrylistEntryDriverSideListLayout) {
        VerticalLayout entrylistDriverLayout = new VerticalLayout();
        entrylistDriverLayout.setPadding(false);
        entrylistDriverLayout.setSpacing(false);
        entrylistDriverLayout.getStyle()
                .setBorder("1px solid var(--lumo-contrast-10pct)")
                .setBorderRadius("var(--lumo-border-radius-m)")
                .setPadding("var(--lumo-space-m)");

        TextField firstNameField = new TextField("First Name");
        firstNameField.setValue(Objects.requireNonNullElse(driver.getFirstName(), ""));
        firstNameField.addValueChangeListener(event -> {
            if (event.getValue() == null || event.getValue().isEmpty()) {
                driver.setFirstName(null);
            } else {
                driver.setFirstName(event.getValue());
            }
            refreshEntrylistPreview();
        });

        TextField lastNameField = new TextField("Last Name");
        lastNameField.setValue(Objects.requireNonNullElse(driver.getLastName(), ""));
        lastNameField.addValueChangeListener(event -> {
            if (event.getValue() == null || event.getValue().isEmpty()) {
                driver.setLastName(null);
            } else {
                driver.setLastName(event.getValue());
            }
            refreshEntrylistPreview();
        });

        TextField shortNameField = new TextField("Short Name");
        shortNameField.setValue(Objects.requireNonNullElse(driver.getShortName(), ""));
        shortNameField.setMinLength(3);
        shortNameField.setMaxLength(3);
        shortNameField.addValueChangeListener(event -> {
            if (event.getValue() == null || event.getValue().isEmpty()) {
                driver.setShortName(null);
            } else if (event.getValue().length() == 3) {
                driver.setShortName(event.getValue());
            }
            refreshEntrylistPreview();
        });

        TextField playerIdField = new TextField("Steam ID");
        playerIdField.setRequired(true);
        playerIdField.setValue(Objects.requireNonNullElse(driver.getPlayerId(), ""));
        playerIdField.addValueChangeListener(event -> {
            if (event.getValue() != null || !event.getValue().isEmpty()) {
                driver.setPlayerId(event.getValue());
            }
            refreshEntrylistPreview();
        });

        ComboBox<AccDriverCategory> driverCategoryComboBox = new ComboBox<>("Category");
        driverCategoryComboBox.setItems(AccDriverCategory.values());
        driverCategoryComboBox.setItemLabelGenerator(AccDriverCategory::getName);
        driverCategoryComboBox.setRenderer(EntrylistRenderer.createAccDriverCategoryRenderer());
        driverCategoryComboBox.setValue(driver.getDriverCategory());
        driverCategoryComboBox.addValueChangeListener(event -> {
            driver.setDriverCategory(event.getValue());
            refreshEntrylistPreview();
        });

        ComboBox<AccNationality> nationalyComboBox = new ComboBox<>("Nationality");
        nationalyComboBox.setItems(AccNationality.values());
        nationalyComboBox.setItemLabelGenerator(AccNationality::getShortName);
        nationalyComboBox.setRenderer(EntrylistRenderer.createAccNationalityRenderer());
        nationalyComboBox.setValue(driver.getNationality());
        nationalyComboBox.addValueChangeListener(event -> {
            driver.setNationality(event.getValue());
            refreshEntrylistPreview();
        });
        nationalyComboBox.setOverlayWidth("225px");

        FormLayout entrylistDriverFormLayout = new FormLayout();
        entrylistDriverFormLayout.add(firstNameField, lastNameField, shortNameField, playerIdField, driverCategoryComboBox, nationalyComboBox);
        entrylistDriverFormLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3));

        Button removeDriverButton = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
        removeDriverButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        removeDriverButton.setAriaLabel("Remove driver");
        removeDriverButton.addClickListener(event -> {
            if (entry.getDrivers().size() > 1) {
                entry.getDrivers().remove(driver);
                refreshEntrylistPreview();

                entrylistEntryDriverSideListLayout.remove(entrylistDriverLayout);
            } else {
                notificationService.showWarningNotification("At least one driver is required");
            }
        });

        Button cloneDriverButton = new Button("Clone");
        cloneDriverButton.setAriaLabel("Clone driver");
        cloneDriverButton.addClickListener(event -> {
            List<AccDriver> drivers = entry.getDrivers();
            if (drivers.size() < AccEntrylistEntry.MAX_DRIVERS) {
                AccDriver clonedDriver = new AccDriver(driver);
                drivers.add(clonedDriver);
                refreshEntrylistPreview();

                entrylistEntryDriverSideListLayout.add(createEntrylistDriverLayout(clonedDriver, entry, entrylistEntryDriverSideListLayout));
            } else {
                notificationService.showWarningNotification("Maximum number of drivers reached");
            }
        });


        Button upButton = new Button(new Icon(VaadinIcon.ARROW_UP));
        upButton.addClickListener(event -> {
            int index = entry.getDrivers().indexOf(driver);
            if (index > 0) {
                Collections.swap(entry.getDrivers(), index, index - 1);
                refreshEntrylistPreview();

                entrylistEntryDriverSideListLayout.remove(entrylistDriverLayout);
                entrylistEntryDriverSideListLayout.addComponentAtIndex(index - 1, createEntrylistDriverLayout(driver, entry, entrylistEntryDriverSideListLayout));
            }
        });

        Button downButton = new Button(new Icon(VaadinIcon.ARROW_DOWN));
        downButton.addClickListener(event -> {
            int index = entry.getDrivers().indexOf(driver);
            if (index < entry.getDrivers().size() - 1) {
                Collections.swap(entry.getDrivers(), index, index + 1);
                refreshEntrylistPreview();

                entrylistEntryDriverSideListLayout.remove(entrylistDriverLayout);
                entrylistEntryDriverSideListLayout.addComponentAtIndex(index + 1, createEntrylistDriverLayout(driver, entry, entrylistEntryDriverSideListLayout));
            }
        });
        downButton.getStyle()
                .setMarginRight("auto");

        FlexLayout driverHeaderLayout = new FlexLayout(cloneDriverButton, upButton, downButton, removeDriverButton);
        driverHeaderLayout.setWidthFull();
        driverHeaderLayout.getStyle()
                .set("gap", "var(--lumo-space-s)");

        entrylistDriverLayout.add(driverHeaderLayout, entrylistDriverFormLayout);
        return entrylistDriverLayout;
    }

    public void setBackGroundColorForServerAdmins(Component component, boolean isServerAdmin) {
        if (isServerAdmin) {
            component.getStyle()
                    .set("background-color", "var(--lumo-primary-color-10pct)");
        } else {
            component.getStyle()
                    .remove("background-color");
        }
    }

    private Dialog createValidationDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Entrylist validation");

        Checkbox selectAllCheckbox = new Checkbox("Select all");

        CheckboxGroup<ValidationRule> validationRulesCheckboxGroup = new CheckboxGroup<>();
        validationRulesCheckboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        validationRulesCheckboxGroup.setLabel("Validation Rules");
        validationRulesCheckboxGroup.setItems(ValidationRule.values());
        validationRulesCheckboxGroup.setRenderer(new ComponentRenderer<Component, ValidationRule>(validationRule -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(Alignment.CENTER);
            layout.setPadding(false);
            layout.setSpacing(false);
            layout.getStyle()
                    .set("gap", "var(--lumo-space-s)");

            Text validationRuleName = new Text(validationRule.getFriendlyName());

            FontIcon infoIcon = new FontIcon("fa-solid", "fa-circle-info");
            infoIcon.setSize("var(--lumo-font-size-m)");

            Tooltip.forComponent(infoIcon)
                    .withText(validationRule.getDescription())
                    .withPosition(Tooltip.TooltipPosition.END);

            layout.add(validationRuleName, infoIcon);
            return layout;
        }));
        validationRulesCheckboxGroup.select(ValidationRule.enabledByDefault());
        validationRulesCheckboxGroup.addValueChangeListener(event -> {
            if (event.getValue().size() == ValidationRule.values().length) {
                selectAllCheckbox.setValue(true);
                selectAllCheckbox.setIndeterminate(false);
            } else if (event.getValue().isEmpty()) {
                selectAllCheckbox.setValue(false);
                selectAllCheckbox.setIndeterminate(false);
            } else {
                selectAllCheckbox.setIndeterminate(true);
            }
        });

        if (validationRulesCheckboxGroup.getSelectedItems().size() == ValidationRule.values().length) {
            selectAllCheckbox.setValue(true);
        } else if (validationRulesCheckboxGroup.getSelectedItems().isEmpty()) {
            selectAllCheckbox.setValue(false);
        } else {
            selectAllCheckbox.setIndeterminate(true);
        }
        selectAllCheckbox.addValueChangeListener(event -> {
            if (selectAllCheckbox.getValue()) {
                validationRulesCheckboxGroup.select(ValidationRule.values());
            } else {
                validationRulesCheckboxGroup.deselectAll();
            }
        });

        VerticalLayout validationRulesLayout = new VerticalLayout();
        validationRulesLayout.setPadding(false);
        validationRulesLayout.setSpacing(false);
        validationRulesLayout.add(selectAllCheckbox, validationRulesCheckboxGroup);

        dialog.add(validationRulesLayout);

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelButton.getStyle()
                .set("margin-right", "auto");

        Button validateButton = new Button("Validate");
        validateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        validateButton.addClickListener(event -> {
            validateEntrylist(validationRulesCheckboxGroup.getSelectedItems());
            dialog.close();
        });

        dialog.getFooter().add(cancelButton, validateButton);

        return dialog;
    }

    private Dialog createResultsUploadDialog() {
        AtomicReference<AccSession> uploadedSession = new AtomicReference<>();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Upload results");

        IntegerField gridStartPositionField = new IntegerField("Grid start position");
        gridStartPositionField.setWidthFull();
        gridStartPositionField.setMin(1);
        gridStartPositionField.setMax(120);
        gridStartPositionField.setPlaceholder("optional");
        gridStartPositionField.setHelperText("Set the starting position for the grid. Leave empty to start from the first position.");

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelButton.getStyle()
                .set("margin-right", "auto");

        Button validateButton = new Button("Update");
        validateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        validateButton.setEnabled(false);
        validateButton.addClickListener(event -> {
            updateEntrylistFromResults(uploadedSession.get(), Optional.ofNullable(gridStartPositionField.getValue()));
            dialog.close();
        });

        dialog.getFooter().add(cancelButton, validateButton);

        Upload resultsUpload = new Upload();
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        resultsUpload.setReceiver(memoryBuffer);
        resultsUpload.setDropAllowed(true);
        resultsUpload.setAcceptedFileTypes(MediaType.APPLICATION_JSON_VALUE);
        resultsUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        resultsUpload.setI18n(configureUploadI18N("results.json"));
        resultsUpload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();

            AccSession session = JsonUtils.fromJson(fileData, AccSession.class);

            // Validate results file against syntax and semantic rules
            validationService.validate(session);

            uploadedSession.set(session);
            validateButton.setEnabled(true);
        });
        resultsUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getErrorMessage()));
        resultsUpload.addFailedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getReason().getMessage()));
        resultsUpload.addFileRemovedListener(event -> validateButton.setEnabled(false));

        Paragraph importantNote = new Paragraph("IMPORTANT: The car number (raceNumber) will be used to match the results with the entrylist entries. Make sure that the car numbers are matching.");
        importantNote.getStyle()
                .setFontWeight(Style.FontWeight.BOLD)
                .setMarginTop("var(--lumo-space-l)");

        Paragraph updateAttributesText = new Paragraph("The session results will be used to update the following fields of the entrylist:");
        UnorderedList updatedAttributesList = new UnorderedList();
        updatedAttributesList.add(new ListItem("Default grid position"));

        dialog.add(resultsUpload, gridStartPositionField, importantNote, updateAttributesText, updatedAttributesList);
        return dialog;
    }

    private Dialog createCustomCarsUploadDialog() {
        AtomicReference<CustomCar[]> uploadedCustomCars = new AtomicReference<>();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Upload default custom cars");

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelButton.getStyle()
                .set("margin-right", "auto");

        Button validateButton = new Button("Update");
        validateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        validateButton.setEnabled(false);
        validateButton.addClickListener(event -> {
            updateEntrylistFromCustomCars(uploadedCustomCars.get());
            dialog.close();
        });

        dialog.getFooter().add(cancelButton, validateButton);

        Upload defaultCustomCarUpload = new Upload();
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        defaultCustomCarUpload.setReceiver(memoryBuffer);
        defaultCustomCarUpload.setDropAllowed(true);
        defaultCustomCarUpload.setAcceptedFileTypes(MediaType.APPLICATION_JSON_VALUE);
        defaultCustomCarUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        defaultCustomCarUpload.setI18n(configureUploadI18N("custom_cars.json"));
        defaultCustomCarUpload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();

            CustomCar[] customCars = JsonUtils.fromJson(fileData, CustomCar[].class);

            // Validate results file against syntax and semantic rules
            for (CustomCar customCar : customCars) {
                validationService.validate(customCar);
            }

            uploadedCustomCars.set(customCars);
            validateButton.setEnabled(true);
        });
        defaultCustomCarUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getErrorMessage()));
        defaultCustomCarUpload.addFailedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getReason().getMessage()));
        defaultCustomCarUpload.addFileRemovedListener(event -> validateButton.setEnabled(false));

        Paragraph updateAttributesText = new Paragraph("The custom cars file will be used to update the following fields of the entrylist:");
        UnorderedList updatedAttributesList = new UnorderedList();
        updatedAttributesList.add(new ListItem("Custom Car"));
        updatedAttributesList.add(new ListItem("Override car model for custom car"));

        dialog.add(defaultCustomCarUpload, updateAttributesText, updatedAttributesList);
        return dialog;
    }

    private SortingModeEntrylist getSortingMode() {
        return sortingModeSelect.getValue();
    }

    private SortingDirection getSortingDirection() {
        return sortdirectionSelect.getValue();
    }

    private void validateEntrylist(Set<ValidationRule> validationRules) {
        if (entrylist == null) {
            notificationService.showErrorNotification("Validation failed - Entrylist is missing");
            return;
        }

        ValidationData validationData = entrylistService.validateRules(entrylist, validationRules);
        if (validationData.getErrors().isEmpty()) {
            createSuccessNotification(entrylistMetadata.getFileName(), "Validation passed");
        } else {
            for (ValidationError validationError : validationData.getErrors()) {
                createErrorNotification(entrylistMetadata.getFileName(), validationError);
            }
        }
    }

    private void createNewEntrylist(AccEntrylist entrylist, EntrylistMetadata entrylistMetadata) {
        resetEntities();
        this.entrylist = entrylist;
        this.entrylistMetadata = entrylistMetadata;
        this.downloadAnchor.setHref(downloadEntrylist(entrylistMetadata));
        refreshEntrylistEditor();
        refreshEntrylistPreview();
    }

    private ConfirmDialog createNewEntrylistConfirmDialog() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Create new entrylist");
        confirmDialog.setText("Your current entrylist will be discarded. Do you want to proceed?");
        confirmDialog.setConfirmText("Continue");
        confirmDialog.setCancelable(true);
        confirmDialog.setCancelButtonTheme("tertiary error");
        confirmDialog.setConfirmButtonTheme("primary error");
        return confirmDialog;
    }

    private ConfirmDialog createResetDialog() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Reset current entrylist");
        confirmDialog.setText("Do you really want to discard the current entrylist?");
        confirmDialog.setConfirmText("Reset");
        confirmDialog.addConfirmListener(event -> reset());
        confirmDialog.setCancelable(true);
        confirmDialog.setCancelButtonTheme("tertiary error");
        confirmDialog.setConfirmButtonTheme("primary error");
        return confirmDialog;
    }

    private void createSuccessNotification(String fileName, String message) {
        Div header = new Div(new Text(fileName));
        header.getStyle()
                .setFontSize("var(--lumo-font-size-m)")
                .setFontWeight(Style.FontWeight.BOLD);

        Div description = new Div(new Text(message));
        description.getStyle()
                .setFontSize("var(--lumo-font-size-s)");

        Div messageContainer = new Div(header, description);

        notificationService.showSuccessNotification(messageContainer);
    }

    private void createErrorNotification(String fileName, ValidationError validationError) {
        List<Object> errorReferences = validationError.getReferences();
        ValidationRule validationRule = validationError.getRule();

        Div header = new Div(new Text(fileName));
        header.getStyle()
                .setFontSize("var(--lumo-font-size-m)")
                .setFontWeight(Style.FontWeight.BOLD);

        Div description = new Div(new Text(validationRule.getFriendlyName() + TEXT_DELIMITER + validationError.getMessage()));
        description.getStyle()
                .setFontSize("var(--lumo-font-size-s)");

        Div messageContainer = new Div(header, description);

        // Add dynamic dialog with error references if available
        if (errorReferences != null && !errorReferences.isEmpty()) {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle(validationRule.getFriendlyName());
            dialog.setModal(false);
            dialog.setDraggable(true);
            dialog.setResizable(true);

            VerticalLayout dialogLayout = new VerticalLayout();
            dialogLayout.setPadding(false);

            Text errorMessage = new Text(validationError.getMessage());
            dialogLayout.add(errorMessage);

            for (Object reference : errorReferences) {
                Span referenceSpan = new Span(JsonUtils.toJsonPretty(reference));
                referenceSpan.setWhiteSpace(HasText.WhiteSpace.PRE_LINE);

                dialogLayout.add(new Div(referenceSpan));
            }

            dialog.add(dialogLayout);

            Button closeButton = new Button(LumoIcon.CROSS.create(), event -> dialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getHeader().add(closeButton);

            Anchor dialogLink = new Anchor("javascript:void(0)", "Show details");
            dialogLink.getElement().addEventListener("click", event -> dialog.open());
            dialogLink.getStyle()
                    .setFontSize("var(--lumo-font-size-s)");

            messageContainer.add(new Div(dialogLink));
        }

        notificationService.showErrorNotification(Duration.ZERO, messageContainer);
    }
}
