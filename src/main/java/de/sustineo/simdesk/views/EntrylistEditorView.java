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
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.EntrylistMetadata;
import de.sustineo.simdesk.entities.EntrylistSortingMode;
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
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Profile(ProfileManager.PROFILE_ENTRYLIST)
@Log
@CssImport("flag-icons/css/flag-icons.min.css")
@Route(value = "/entrylist/editor")
@PageTitle("Entrylist - Editor")
@AnonymousAllowed
public class EntrylistEditorView extends BaseView {
    private final EntrylistService entrylistService;
    private final ValidationService validationService;
    private final NotificationService notificationService;

    private AccEntrylist entrylist;
    private EntrylistMetadata entrylistMetadata;

    private final Upload entrylistUpload = new Upload();
    private final Anchor downloadAnchor = new Anchor();
    private final Select<EntrylistSortingMode> sortingModeSelect = new Select<>();
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

    public EntrylistEditorView(EntrylistService entrylistService,
                               ValidationService validationService,
                               NotificationService notificationService) {
        this.entrylistService = entrylistService;
        this.validationService = validationService;
        this.notificationService = notificationService;

        this.entrylistPreview.setWidthFull();
        this.entrylistPreview.setReadOnly(true);

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createEntrylistContainer());
        add(createFooter());
    }

    private void resetForm() {
        entrylist = null;
        entrylistMetadata = null;
        entrylistUpload.clearFileList();
        refreshEntrylistEditor();
    }

    private void refreshEntrylistPreview() {
        if (entrylist != null) {
            entrylistPreview.setValue(JsonUtils.toJsonPretty(entrylist));
        } else {
            entrylistPreview.clear();
        }
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
        entrylistContainer.add(createPopulateEntrylistLayout(), createActionLayout(), createSortingLayout(), createTabSheets());

        Div entrylistContainerWrapper = new Div(entrylistContainer);
        entrylistContainerWrapper.addClassNames("container", "bg-light");

        return entrylistContainerWrapper;
    }

    private Component createPopulateEntrylistLayout() {
        Span spacer = new Span(new Text("OR"));
        spacer.getStyle()
                .setFontWeight(Style.FontWeight.BOLD);

        VerticalLayout spacerLayout = new VerticalLayout();
        spacerLayout.setPadding(false);
        spacerLayout.setAlignItems(Alignment.CENTER);
        spacerLayout.add(spacer);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.add(createNewEntrylistLayout(), spacerLayout, createFileUploadLayout());

        return layout;
    }

    private Component createNewEntrylistLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setAlignItems(Alignment.CENTER);

        ConfirmDialog createNewEntrylistConfirmDialog = createNewEntrylistConfirmDialog();
        createNewEntrylistConfirmDialog.addConfirmListener(event -> createNewEntrylist(new AccEntrylist(), new EntrylistMetadata()));

        Button createEntrylistButton = new Button("Create new entrylist");
        createEntrylistButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createEntrylistButton.addClickListener(event -> {
            if (this.entrylist != null) {
                createNewEntrylistConfirmDialog.open();
            } else {
                createNewEntrylist(new AccEntrylist(), new EntrylistMetadata());
            }
        });

        layout.add(createEntrylistButton);

        return layout;
    }

    private Component createFileUploadLayout() {
        VerticalLayout fileUploadLayout = new VerticalLayout();
        fileUploadLayout.setWidthFull();
        fileUploadLayout.setPadding(false);
        fileUploadLayout.setSpacing(false);

        Paragraph fileUploadHint = new Paragraph("Accepted file formats: JSON (.json). File size must be less than or equal to 1 MB.");
        fileUploadHint.getStyle()
                .setFontSize("var(--lumo-font-size-s)")
                .setColor("var(--lumo-secondary-text-color)");

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        entrylistUpload.setReceiver(memoryBuffer);
        entrylistUpload.setWidthFull();
        entrylistUpload.setDropAllowed(true);
        entrylistUpload.setAcceptedFileTypes(MediaType.APPLICATION_JSON_VALUE);
        entrylistUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        entrylistUpload.setI18n(configureUploadI18N());
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
                    createValidationSuccessNotification(entrylistMetadata.getFileName(), "File uploaded successfully");
                });
                createNewEntrylistConfirmDialog.addCancelListener(dialogEvent -> entrylistUpload.clearFileList());
                createNewEntrylistConfirmDialog.open();
            } else {
                createNewEntrylist(entrylist, entrylistMetadata);
                createValidationSuccessNotification(entrylistMetadata.getFileName(), "File uploaded successfully");
            }
        });
        entrylistUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getErrorMessage()));
        entrylistUpload.addFailedListener(event -> notificationService.showErrorNotification(event.getReason().getMessage()));
        entrylistUpload.addFileRemovedListener(event -> resetDialog.open());

        fileUploadLayout.add(entrylistUpload, fileUploadHint);
        return fileUploadLayout;
    }

    private UploadI18N configureUploadI18N() {
        UploadI18NDefaults i18n = new UploadI18NDefaults();
        i18n.getAddFiles().setOne("Upload entrylist.json...");
        i18n.getDropFiles().setOne("Drop entrylist.json here");
        i18n.getError().setIncorrectFileType("The provided file does not have the correct format (.json)");
        i18n.getError().setFileIsTooBig("The provided file is too big. Maximum file size is 1 MB");
        return i18n;
    }

    private Component createActionLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Download
        Button downloadButton = new Button("Download", getDownloadIcon());
        downloadButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        this.downloadAnchor.getElement().setAttribute("download", true);
        this.downloadAnchor.add(downloadButton);

        // Validation
        Button validateButton = new Button("Validate");
        validateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        validateButton.addClickListener(e -> validationDialog.open());

        // Reset
        Button resetButton = new Button("Reset");
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

    private Component createSortingLayout() {
        this.sortingModeSelect.setLabel("Sort by");
        this.sortingModeSelect.setItems(EntrylistSortingMode.values());
        this.sortingModeSelect.setItemLabelGenerator(EntrylistSortingMode::getLabel);
        this.sortingModeSelect.setValue(EntrylistSortingMode.NONE);
        this.sortingModeSelect.addValueChangeListener(event -> {
            refreshEntrylistEntriesFromMap();
        });

        HorizontalLayout entrylistSortingLayout = new HorizontalLayout(sortingModeSelect);
        entrylistSortingLayout.setWidthFull();
        entrylistSortingLayout.setJustifyContentMode(JustifyContentMode.END);

        return entrylistSortingLayout;
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

        entrylistLayout.add(createEntrylistMainLayout(), entrylistEntriesLayout, createEntrylistActionLayout());
    }

    private void refreshEntrylistEntriesFromMap() {
        Comparator<Map.Entry<AccEntrylistEntry, Component>> comparator = switch (getEntrylistSortingMode()) {
            case GRID_POSITION -> gridPositionComparator;
            case CAR_NUMBER -> raceNumberComparator;
            case ADMIN -> adminComparator.thenComparing(raceNumberComparator);
            default -> noopComparator;
        };

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

    private Component createEntrylistActionLayout() {
        Button addEntrylistEntryButton = new Button("Add entry");
        addEntrylistEntryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEntrylistEntryButton.addClickListener(event -> {
            addEntrylistEntry(new AccEntrylistEntry());
        });

        HorizontalLayout entrylistActionLayout = new HorizontalLayout(addEntrylistEntryButton);
        entrylistActionLayout.setWidthFull();
        entrylistActionLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        return entrylistActionLayout;
    }

    private Component createEntrylistMainLayout() {
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

            if (EntrylistSortingMode.CAR_NUMBER.equals(getEntrylistSortingMode())) {
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
        ComboBox.ItemFilter<Car> carFilter = (car, filterString) -> car.getCarName().toLowerCase().contains(filterString.toLowerCase()) || car.getCarGroup().name().equalsIgnoreCase(filterString);
        forcedCarModelComboBox.setItems(carFilter, Car.getAllSortedByName());
        forcedCarModelComboBox.setItemLabelGenerator(Car::getCarName);
        forcedCarModelComboBox.setClassNameGenerator(car -> car.getCarGroup().name());
        forcedCarModelComboBox.setValue(Car.getCarById(entry.getForcedCarModel()));
        forcedCarModelComboBox.addValueChangeListener(event -> {
            Integer carId = Optional.of(event)
                    .map(ComboBox.ValueChangeEvent::getValue)
                    .map(Car::getCarId)
                    .orElse(AccEntrylistEntry.DEFAULT_FORCED_CAR_MODEL);

            entry.setForcedCarModel(carId);
            refreshEntrylistPreview();
        });

        Checkbox overrideCarModelForCustomCarCheckbox = new Checkbox("Enabled");
        overrideCarModelForCustomCarCheckbox.setTooltipText("Enable this option to override the car model for the custom car");
        overrideCarModelForCustomCarCheckbox.setValue(Integer.valueOf(1).equals(entry.getOverrideCarModelForCustomCar()));

        CheckboxGroup<Checkbox> overrideCarModelForCustomCarCheckboxGroup = new CheckboxGroup<>("Override car model for custom car");
        overrideCarModelForCustomCarCheckboxGroup.setTooltipText("Override car model for custom car");
        overrideCarModelForCustomCarCheckboxGroup.setItems(overrideCarModelForCustomCarCheckbox);
        overrideCarModelForCustomCarCheckboxGroup.setItemLabelGenerator(Checkbox::getLabel);
        overrideCarModelForCustomCarCheckboxGroup.addValueChangeListener(event -> {
            entry.setOverrideCarModelForCustomCar(event.getValue().contains(overrideCarModelForCustomCarCheckbox) ? 1 : 0);
            refreshEntrylistPreview();
        });

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

            if (EntrylistSortingMode.GRID_POSITION.equals(getEntrylistSortingMode())) {
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

            if (EntrylistSortingMode.ADMIN.equals(getEntrylistSortingMode())) {
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
        removeEntrylistEntryButton.addClickListener(event -> {
            removeEntrylistEntry(entry);
        });

        Button cloneEntrylistEntryButton = new Button("Clone");
        cloneEntrylistEntryButton.setAriaLabel("Clone entry");
        cloneEntrylistEntryButton.addClickListener(event -> {
            addEntrylistEntry(new AccEntrylistEntry(entry));
        });

        HorizontalLayout entrylistEntryHeaderLayout = new HorizontalLayout(cloneEntrylistEntryButton, removeEntrylistEntryButton);
        entrylistEntryHeaderLayout.setWidthFull();
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

        HorizontalLayout driverHeaderLayout = new HorizontalLayout(cloneDriverButton, removeDriverButton);
        driverHeaderLayout.setWidthFull();
        driverHeaderLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

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
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(cancelButton);

        Button validateButton = new Button("Validate", (e) -> {
            validateEntrylist(validationRulesCheckboxGroup.getSelectedItems());
            dialog.close();
        });
        validateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(validateButton);

        return dialog;
    }

    private EntrylistSortingMode getEntrylistSortingMode() {
        return sortingModeSelect.getValue();
    }

    private void validateEntrylist(Set<ValidationRule> validationRules) {
        if (entrylist == null) {
            notificationService.showErrorNotification("Validation failed. Entrylist is missing");
            return;
        }

        ValidationData validationData = entrylistService.validateRules(entrylist, validationRules);
        if (validationData.getErrors().isEmpty()) {
            createValidationSuccessNotification(entrylistMetadata.getFileName(), "Validation passed");
        } else {
            for (ValidationError validationError : validationData.getErrors()) {
                createValidationErrorNotification(entrylistMetadata.getFileName(), validationError);
            }
        }
    }

    private void createNewEntrylist(AccEntrylist entrylist, EntrylistMetadata entrylistMetadata) {
        entrylistUpload.clearFileList();
        this.entrylist = entrylist;
        this.entrylistMetadata = entrylistMetadata;
        this.downloadAnchor.setHref(downloadEntrylist(entrylistMetadata));
        refreshEntrylistEditor();
    }

    private ConfirmDialog createNewEntrylistConfirmDialog() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Create new entrylist");
        confirmDialog.setText("Your current entrylist will be discarded. Do you want to proceed?");
        confirmDialog.setConfirmText("Continue");
        confirmDialog.setCancelable(true);
        return confirmDialog;
    }

    private ConfirmDialog createResetDialog() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Reset current entrylist");
        confirmDialog.setText("Do you really want to discard the current entrylist?");
        confirmDialog.setConfirmText("Reset");
        confirmDialog.addConfirmListener(event -> resetForm());
        confirmDialog.setCancelable(true);
        return confirmDialog;
    }

    private void createValidationSuccessNotification(String fileName, String message) {
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

    private void createValidationErrorNotification(String fileName, ValidationError validationError) {
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
