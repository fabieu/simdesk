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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.entrylist.Entry;
import de.sustineo.simdesk.entities.entrylist.Entrylist;
import de.sustineo.simdesk.entities.entrylist.EntrylistMetadata;
import de.sustineo.simdesk.entities.json.kunos.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.AccDriverCategory;
import de.sustineo.simdesk.entities.json.kunos.AccNationality;
import de.sustineo.simdesk.entities.validation.ValidationData;
import de.sustineo.simdesk.entities.validation.ValidationError;
import de.sustineo.simdesk.entities.validation.ValidationRule;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.ValidationService;
import de.sustineo.simdesk.services.entrylist.EntrylistService;
import de.sustineo.simdesk.utils.json.JsonUtils;
import de.sustineo.simdesk.views.i18n.UploadI18NDefaults;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Profile(ProfileManager.PROFILE_ENTRYLIST)
@Log
@Route(value = "/entrylist/editor", layout = MainLayout.class)
@PageTitle("Entrylist - Editor")
@AnonymousAllowed
public class EntrylistEditorView extends BaseView {
    private final EntrylistService entrylistService;
    private final ValidationService validationService;
    private final NotificationService notificationService;

    private Entrylist entrylist;
    private EntrylistMetadata entrylistMetadata;

    private Upload entrylistUpload;
    private final TextArea entrylistOutput;
    private final VerticalLayout entrylistEditorLayout;

    private final ConfirmDialog resetDialog = createResetDialog();
    private final Dialog validationDialog = createValidationDialog();

    public EntrylistEditorView(EntrylistService entrylistService,
                               ValidationService validationService,
                               NotificationService notificationService) {
        this.entrylistService = entrylistService;
        this.validationService = validationService;
        this.notificationService = notificationService;

        this.entrylistEditorLayout = new VerticalLayout();
        this.entrylistOutput = new TextArea();
        this.entrylistOutput.setWidthFull();
        this.entrylistOutput.setReadOnly(true);

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createEntrylistForm());
        add(createFooter());
    }

    private void resetForm() {
        entrylist = null;
        entrylistMetadata = null;
        entrylistUpload.clearFileList();
        refreshEntrylistEditor();
        refreshEntrylistOutput();
    }

    private void refreshEntrylistOutput() {
        if (entrylist != null) {
            entrylistOutput.setValue(JsonUtils.toJsonPretty(entrylist));
        } else {
            entrylistOutput.clear();
        }
    }

    private Component createEntrylistForm() {
        Div layout = new Div();
        layout.addClassNames("container", "bg-light");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSizeFull();
        formLayout.setPadding(false);
        formLayout.add(entrylistCreateLayout(), buttonLayout(), entrylistEditorLayout, entrylistOutput);

        layout.add(formLayout);
        return layout;
    }

    private Component entrylistCreateLayout() {
        Span spacer = new Span(new Text("OR"));
        spacer.getStyle()
                .setFontWeight(Style.FontWeight.BOLD);

        VerticalLayout spacerLayout = new VerticalLayout();
        spacerLayout.setPadding(false);
        spacerLayout.setAlignItems(Alignment.CENTER);
        spacerLayout.add(spacer);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.add(newEntrylistLayout(), spacerLayout, fileUploadLayout());

        return layout;
    }

    private Component newEntrylistLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setAlignItems(Alignment.CENTER);

        ConfirmDialog createEntrylistDialog = createEntrylistDialog();
        createEntrylistDialog.addConfirmListener(event -> {
            resetForm();
            this.entrylist = new Entrylist();
            refreshEntrylistEditor();
            refreshEntrylistOutput();
        });

        Button createEntrylistButton = new Button("Create new entrylist");
        createEntrylistButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createEntrylistButton.addClickListener(event -> createEntrylistDialog.open());

        layout.add(createEntrylistButton);

        return layout;
    }

    private Component fileUploadLayout() {
        VerticalLayout fileUploadLayout = new VerticalLayout();
        fileUploadLayout.setWidthFull();
        fileUploadLayout.setPadding(false);
        fileUploadLayout.setSpacing(false);

        Paragraph fileUploadHint = new Paragraph("Accepted file formats: JSON (.json). File size must be less than or equal to 1 MB.");
        fileUploadHint.getStyle()
                .setFontSize("var(--lumo-font-size-s)")
                .setColor("var(--lumo-secondary-text-color)");

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        entrylistUpload = new Upload(memoryBuffer);
        entrylistUpload.setWidthFull();
        entrylistUpload.setDropAllowed(true);
        entrylistUpload.setAcceptedFileTypes(MediaType.APPLICATION_JSON_VALUE);
        entrylistUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        entrylistUpload.setI18n(configureUploadI18N());
        entrylistUpload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();

            Entrylist entrylist = JsonUtils.fromJson(fileData, Entrylist.class);
            EntrylistMetadata entrylistMetadata = EntrylistMetadata.builder()
                    .fileName(event.getFileName())
                    .type(event.getMIMEType())
                    .contentLength(event.getContentLength())
                    .build();

            // Validate entrylist file against syntax and semantic rules
            validationService.validate(entrylist);

            ConfirmDialog dialog = createEntrylistDialog();
            dialog.addConfirmListener(dialogEvent -> {
                this.entrylist = entrylist;
                this.entrylistMetadata = entrylistMetadata;
                refreshEntrylistEditor();
                refreshEntrylistOutput();

                createValidationSuccessNotification(entrylistMetadata.getFileName(), "File uploaded successfully");
            });
            dialog.addCancelListener(dialogEvent -> entrylistUpload.clearFileList());
            dialog.open();
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

    private Component buttonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Validation
        Button validateButton = new Button("Validate");
        validateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        validateButton.addClickListener(e -> validationDialog.open());

        // Reset
        Button resetButton = new Button("Reset");
        resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetButton.addClickListener(e -> resetDialog.open());

        buttonLayout.add(validateButton, resetButton);
        return buttonLayout;
    }

    private void refreshEntrylistEditor() {
        entrylistEditorLayout.removeAll();

        if (entrylist == null) {
            return;
        }

        entrylistEditorLayout.add(createEntrylistBaseInformation());

        List<Entry> entries = Optional.of(entrylist)
                .map(Entrylist::getEntries)
                .orElse(Collections.emptyList());

        for (Entry entry : entries) {
            entrylistEditorLayout.add(createEntrylistEntryLayout(entry));
        }
    }

    private Component createEntrylistBaseInformation() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        Checkbox forceEntrylistCheckbox = new Checkbox();
        forceEntrylistCheckbox.setLabel("Force entrylist");
        forceEntrylistCheckbox.setValue(Integer.valueOf(1).equals(entrylist.getForceEntryList()));
        forceEntrylistCheckbox.addValueChangeListener(event -> {
            entrylist.setForceEntryList(forceEntrylistCheckbox.getValue() ? 1 : 0);
            refreshEntrylistOutput();
        });

        layout.add(forceEntrylistCheckbox);
        return layout;
    }

    private Component createEntrylistEntryLayout(Entry entry) {
        Div entryLayout = new Div();
        entryLayout.setWidthFull();
        entryLayout.addClassNames("pure-g");
        entryLayout.getStyle()
                .setBorder("1px solid var(--lumo-contrast-10pct)")
                .setBorderRadius("var(--lumo-border-radius-m)");

        // Race number
        IntegerField raceNumberField = new IntegerField("Car Number");
        raceNumberField.setValue(entry.getRaceNumber());
        raceNumberField.setPrefixComponent(new Span("#"));
        raceNumberField.setMin(0);
        raceNumberField.setMax(999);
        raceNumberField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setRaceNumber(null);
            } else if (event.getValue() >= 0 && event.getValue() <= 999) {
                entry.setRaceNumber(event.getValue());
            }
            refreshEntrylistOutput();
        });

        // Ballast
        IntegerField ballastField = new IntegerField("Ballast");
        ballastField.setValue(entry.getBallastKg());
        ballastField.setSuffixComponent(new Span("kg"));
        ballastField.setMin(-40);
        ballastField.setMax(40);
        ballastField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setBallastKg(null);
            } else if (event.getValue() >= -40 && event.getValue() <= 40) {
                entry.setBallastKg(event.getValue());
            }
            refreshEntrylistOutput();
        });

        // Restrictor
        IntegerField restrictorField = new IntegerField("Restrictor");
        restrictorField.setValue(entry.getRestrictor());
        restrictorField.setSuffixComponent(new Span("%"));
        restrictorField.setMin(0);
        restrictorField.setMax(20);
        restrictorField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setRestrictor(null);
            } else if (event.getValue() >= 0 && event.getValue() <= 20) {
                entry.setRestrictor(event.getValue());
            }
            refreshEntrylistOutput();
        });

        ComboBox<Car> forcedCarModelComboBox = new ComboBox<>("Car Model");
        forcedCarModelComboBox.setItems(Car.getAllSortedByName());
        forcedCarModelComboBox.setItemLabelGenerator(Car::getCarName);
        forcedCarModelComboBox.setValue(Car.getCarById(entry.getForcedCarModel()));
        forcedCarModelComboBox.addValueChangeListener(event -> {
            Integer carId = Optional.of(event)
                    .map(ComboBox.ValueChangeEvent::getValue)
                    .map(Car::getCarId)
                    .orElse(null);

            entry.setForcedCarModel(carId);
            refreshEntrylistOutput();
        });

        Checkbox overrideCarModelForCustomCarCheckbox = new Checkbox("Enabled");
        overrideCarModelForCustomCarCheckbox.setTooltipText("Enable this option to override the car model for the custom car");
        overrideCarModelForCustomCarCheckbox.setValue(Integer.valueOf(1).equals(entry.getOverrideCarModelForCustomCar()));
        overrideCarModelForCustomCarCheckbox.addValueChangeListener(event -> {
            entry.setOverrideCarModelForCustomCar(overrideCarModelForCustomCarCheckbox.getValue() ? 1 : 0);
            refreshEntrylistOutput();
        });

        CheckboxGroup<Checkbox> overrideCarModelForCustomCarCheckboxGroup = new CheckboxGroup<>("Override car model for custom car");
        overrideCarModelForCustomCarCheckboxGroup.setTooltipText("Override car model for custom car");
        overrideCarModelForCustomCarCheckboxGroup.setItems(overrideCarModelForCustomCarCheckbox);
        overrideCarModelForCustomCarCheckboxGroup.setItemLabelGenerator(Checkbox::getLabel);

        TextField customCarField = new TextField("Custom Car");
        customCarField.setValue(entry.getCustomCar());
        customCarField.addValueChangeListener(event -> {
            if (event.getValue() == null || event.getValue().isEmpty()) {
                entry.setCustomCar(null);
            } else {
                entry.setCustomCar(event.getValue());
            }
            refreshEntrylistOutput();
        });

        IntegerField defaultGridPositionField = new IntegerField("Grid Position");
        defaultGridPositionField.setValue(entry.getDefaultGridPosition());
        defaultGridPositionField.setMin(1);
        defaultGridPositionField.setMax(120);
        defaultGridPositionField.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                entry.setDefaultGridPosition(null);
            } else if (event.getValue() >= 1 && event.getValue() <= 120) {
                entry.setDefaultGridPosition(event.getValue());
            }
            refreshEntrylistOutput();
        });

        Checkbox isServerAdminCheckbox = new Checkbox("Server Admin");
        isServerAdminCheckbox.setValue(Integer.valueOf(1).equals(entry.getIsServerAdmin()));
        isServerAdminCheckbox.addValueChangeListener(event -> {
            entry.setIsServerAdmin(isServerAdminCheckbox.getValue() ? 1 : 0);
            setBackGroundColorForServerAdmins(entryLayout, isServerAdminCheckbox.getValue());
            refreshEntrylistOutput();
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

        VerticalLayout entrylistMainLayout = new VerticalLayout(entrylistMainFormLayout, isServerAdminCheckbox);
        Div entrylistMainLayoutWrapper = new Div();
        entrylistMainLayoutWrapper.addClassNames("pure-u-1", "pure-u-md-1-2");
        entrylistMainLayoutWrapper.add(entrylistMainLayout);

        Checkbox overrideDriverInfoCheckbox = new Checkbox("Override driver info");
        overrideDriverInfoCheckbox.setValue(entry.getOverrideDriverInfo() == 1);
        overrideDriverInfoCheckbox.addValueChangeListener(event -> {
            entry.setOverrideDriverInfo(overrideDriverInfoCheckbox.getValue() ? 1 : 0);
            refreshEntrylistOutput();
        });

        VerticalLayout entrylistDriverListLayout = new VerticalLayout();
        entrylistDriverListLayout.setPadding(false);

        for (AccDriver driver : entry.getDrivers()) {
            TextField firstNameField = new TextField("First Name");
            firstNameField.setValue(driver.getFirstName());
            firstNameField.addValueChangeListener(event -> {
                if (event.getValue() == null || event.getValue().isEmpty()) {
                    driver.setFirstName(null);
                } else {
                    driver.setFirstName(event.getValue());
                }
                refreshEntrylistOutput();
            });

            TextField lastNameField = new TextField("Last Name");
            lastNameField.setValue(driver.getLastName());
            lastNameField.addValueChangeListener(event -> {
                if (event.getValue() == null || event.getValue().isEmpty()) {
                    driver.setLastName(null);
                } else {
                    driver.setLastName(event.getValue());
                }
                refreshEntrylistOutput();
            });

            TextField shortNameField = new TextField("Short Name");
            shortNameField.setValue(driver.getShortName());
            shortNameField.setMinLength(3);
            shortNameField.setMaxLength(3);
            shortNameField.addValueChangeListener(event -> {
                if (event.getValue() == null || event.getValue().isEmpty()) {
                    driver.setShortName(null);
                } else if (event.getValue().length() == 3) {
                    driver.setShortName(event.getValue());
                }
                refreshEntrylistOutput();
            });

            TextField playerIdField = new TextField("Steam ID");
            playerIdField.setRequired(true);
            playerIdField.setValue(driver.getPlayerId());
            playerIdField.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    driver.setPlayerId(event.getValue());
                }
                refreshEntrylistOutput();
            });

            ComboBox<AccDriverCategory> driverCategorySelect = new ComboBox<>("Category");
            driverCategorySelect.setItems(AccDriverCategory.values());
            driverCategorySelect.setItemLabelGenerator(AccDriverCategory::getName);
            driverCategorySelect.setValue(driver.getDriverCategory());
            driverCategorySelect.addValueChangeListener(event -> {
                driver.setDriverCategory(event.getValue());
                refreshEntrylistOutput();
            });

            ComboBox<AccNationality> nationalyComboBox = new ComboBox<>("Nationality");
            nationalyComboBox.setItems(AccNationality.values());
            nationalyComboBox.setItemLabelGenerator(AccNationality::getName);
            nationalyComboBox.setValue(driver.getNationality());
            nationalyComboBox.addValueChangeListener(event -> {
                driver.setNationality(event.getValue());
                refreshEntrylistOutput();
            });

            FormLayout entrylistDriverFormLayout = new FormLayout();
            entrylistDriverFormLayout.add(firstNameField, lastNameField, shortNameField, playerIdField, driverCategorySelect, nationalyComboBox);
            entrylistDriverFormLayout.setResponsiveSteps(
                    // Use one column by default
                    new FormLayout.ResponsiveStep("0", 1),
                    // Use two columns, if layout's width exceeds 500px
                    new FormLayout.ResponsiveStep("500px", 3));
            entrylistDriverFormLayout.getStyle()
                    .setBorder("1px solid var(--lumo-contrast-10pct)")
                    .setBorderRadius("var(--lumo-border-radius-m)")
                    .setPadding("var(--lumo-space-m)");
            entrylistDriverListLayout.add(entrylistDriverFormLayout);

            setBackGroundColorForServerAdmins(entryLayout, Integer.valueOf(1).equals(entry.getIsServerAdmin()));
        }


        VerticalLayout entrylistDriverLayout = new VerticalLayout(overrideDriverInfoCheckbox, entrylistDriverListLayout);
        Div entrylistDriverLayoutWrapper = new Div();
        entrylistDriverLayoutWrapper.addClassNames("pure-u-1", "pure-u-md-1-2");
        entrylistDriverLayoutWrapper.add(entrylistDriverLayout);

        entryLayout.add(entrylistMainLayoutWrapper, entrylistDriverLayoutWrapper);
        return entryLayout;
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

        ValidationRule[] items = ValidationRule.values();

        Checkbox selectAllCheckbox = new Checkbox("Select all");
        selectAllCheckbox.setValue(true);

        CheckboxGroup<ValidationRule> validationRulesCheckboxGroup = new CheckboxGroup<>();
        validationRulesCheckboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        validationRulesCheckboxGroup.setLabel("Validation Rules");
        validationRulesCheckboxGroup.setItems(items);
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
        validationRulesCheckboxGroup.select(ValidationRule.values());
        validationRulesCheckboxGroup.addValueChangeListener(event -> {
            if (event.getValue().size() == items.length) {
                selectAllCheckbox.setValue(true);
                selectAllCheckbox.setIndeterminate(false);
            } else if (event.getValue().isEmpty()) {
                selectAllCheckbox.setValue(false);
                selectAllCheckbox.setIndeterminate(false);
            } else {
                selectAllCheckbox.setIndeterminate(true);
            }
        });

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

    private void validateEntrylist(Set<ValidationRule> validationRules) {
        if (entrylist == null) {
            notificationService.showErrorNotification("No entrylist file uploaded");
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

    private ConfirmDialog createEntrylistDialog() {
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
