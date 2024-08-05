package de.sustineo.simdesk.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.AccBop;
import de.sustineo.simdesk.entities.json.kunos.AccBopEntry;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.ValidationService;
import de.sustineo.simdesk.utils.json.JsonUtils;
import de.sustineo.simdesk.views.fields.BopEditField;
import de.sustineo.simdesk.views.i18n.UploadI18NDefaults;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Profile(ProfileManager.PROFILE_BOP)
@Log
@Route(value = "/bop/editor", layout = MainLayout.class)
@PageTitle("BoP - Editor")
@AnonymousAllowed
public class BopEditorView extends BaseView {
    private final ValidationService validationService;
    private final NotificationService notificationService;

    private final FormLayout settingsLayout = new FormLayout();
    private final FormLayout carsLayout = new FormLayout();
    private final ComboBox<Track> trackComboBox = new ComboBox<>("Track");
    private final MultiSelectComboBox<Car> carsComboBox = new MultiSelectComboBox<>("Cars");
    private final TextArea previewTextArea = new TextArea("Preview");
    private final LinkedHashMap<Integer, Component> currentCarComponents = new LinkedHashMap<>();
    private AccBop currentBop = new AccBop();

    public BopEditorView(ValidationService validationService,
                         NotificationService notificationService) {
        this.validationService = validationService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createFormLayout());
        add(createFooter());

        reloadComponents();
    }

    private Component createFormLayout() {
        Div layout = new Div();
        layout.addClassNames("container", "bg-light");

        layout.add(createFileUploadForm(), createEditingForm());

        return layout;
    }

    private Component createFileUploadForm() {
        VerticalLayout fileUploadLayout = new VerticalLayout();
        fileUploadLayout.setPadding(false);

        H4 fileUploadTitle = new H4("Upload bop.json");
        Paragraph fileUploadHint = new Paragraph("File size must be less than or equal to 1 MB. Only valid JSON files are accepted.");
        fileUploadHint.getStyle().setColor("var(--lumo-secondary-text-color)");

        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
        Upload fileUpload = new Upload(multiFileMemoryBuffer);
        fileUpload.setWidthFull();
        fileUpload.setDropAllowed(true);
        fileUpload.setAcceptedFileTypes("application/json", ".json");
        fileUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        fileUpload.setI18n(configureUploadI18N());
        fileUpload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream fileData = multiFileMemoryBuffer.getInputStream(fileName);

            try {
                currentBop = JsonUtils.fromJson(fileData, AccBop.class);
                validationService.validate(currentBop);

                if (currentBop.isMultiTrack()) {
                    String errorMessage = String.format("Please upload %s with settings for only one track.", fileName);
                    notificationService.showErrorNotification(errorMessage);
                    return;
                }

                Track track = currentBop.getTrack();
                List<Car> cars = currentBop.getCars();

                trackComboBox.setValue(track);
                carsComboBox.setValue(cars);

                currentBop.getEntries().forEach(entry -> currentCarComponents.put(entry.getCarId(), createBopEditField(entry)));

                reloadComponents();
            } catch (ConstraintViolationException e) {
                throw new RuntimeException("Invalid bop file", e);
            } catch (IOException e) {
                throw new RuntimeException("Invalid JSON file", e);
            }
        });
        fileUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(event.getErrorMessage()));
        fileUpload.addFailedListener(event -> notificationService.showErrorNotification(event.getFileName() + TEXT_DELIMITER + event.getReason().getMessage()));

        fileUploadLayout.add(fileUploadTitle, fileUploadHint, fileUpload);
        return fileUploadLayout;
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

    private BopEditField createBopEditField(AccBopEntry initEntry) {
        BopEditField bopEditField = new BopEditField(initEntry);
        bopEditField.addValueChangeListener(event -> {
            try {
                AccBopEntry changedEntry = event.getValue();
                bopEditField.getBinder().writeBean(changedEntry);

                currentBop.getEntries().forEach(entry -> {
                    if (entry.getCarId().equals(changedEntry.getCarId())) {
                        entry.setBallastKg(changedEntry.getBallastKg());
                        entry.setRestrictor(changedEntry.getRestrictor());
                    }
                });

                reloadComponents();
            } catch (ValidationException ignored) {
            }
        });
        return bopEditField;
    }

    private Component createEditingForm() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);

        trackComboBox.setItems(Track.getAllSortedByName());
        trackComboBox.setItemLabelGenerator(Track::getTrackName);
        trackComboBox.setPlaceholder("Select track...");
        trackComboBox.setHelperText("Available filters: Track Name");
        trackComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                currentBop.getEntries().forEach(entry -> entry.setTrackId(event.getValue().getTrackId()));
                reloadComponents();
            }
        });

        ComboBox.ItemFilter<Car> carFilter = (car, filterString) -> car.getCarName().toLowerCase().contains(filterString.toLowerCase()) || car.getCarGroup().name().equalsIgnoreCase(filterString);
        carsComboBox.setItems(carFilter, Car.getAllSortedByName());
        carsComboBox.setItemLabelGenerator(Car::getCarName);
        carsComboBox.setPlaceholder("Select cars...");
        carsComboBox.setHelperText(String.format("Available filters: Car Name, Car Group (%s)", String.join(", ", CarGroup.getValidNames())));
        carsComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (trackComboBox.isEmpty() && !carsComboBox.isEmpty()) {
                    notificationService.showErrorNotification("Please select a track first!");
                    carsComboBox.clear();
                    return;
                }

                // Add entries and bop edit fields for newly selected cars
                event.getValue().forEach(car -> {
                    if (currentBop.getEntries().stream().noneMatch(entry -> entry.getCarId().equals(car.getCarId()))) {
                        AccBopEntry entry = new AccBopEntry(trackComboBox.getValue().getTrackId(), car.getCarId(), 0, 0);
                        currentBop.getEntries().add(entry);
                        currentCarComponents.put(entry.getCarId(), createBopEditField(entry));
                    }
                });

                // Remove entries and bop edit fields for cars that are not selected anymore
                Set<AccBopEntry> entriesToRemove = new HashSet<>();
                currentBop.getEntries().forEach(entry -> {
                    if (!event.getValue().contains(new Car(entry.getCarId(), entry.getCarName()))) {
                        entriesToRemove.add(entry);
                        currentCarComponents.remove(entry.getCarId());
                    }
                });
                currentBop.getEntries().removeAll(entriesToRemove);

                reloadComponents();
            }
        });

        previewTextArea.setWidthFull();
        previewTextArea.setReadOnly(true);

        settingsLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("850px", 2)
        );
        settingsLayout.add(trackComboBox, carsComboBox);

        carsLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("850px", 2),
                new FormLayout.ResponsiveStep("1700px", 4)
        );

        /* Button layout */
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setAlignItems(Alignment.CENTER);

        Button downloadButton = new Button("Download", getDownloadIcon());
        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Anchor downloadAnchor = new Anchor(downloadBop(), "");
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.removeAll();
        downloadAnchor.add(downloadButton);

        ConfirmDialog resetDialog = new ConfirmDialog();
        resetDialog.setHeader("Reset current settings");
        resetDialog.setText("Do you really want to discard the current settings?");
        resetDialog.setConfirmText("Reset");
        resetDialog.addConfirmListener(event -> resetBop());
        resetDialog.setCancelable(true);

        Button resetButton = new Button("Reset");
        resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetButton.addClickListener(e -> resetDialog.open());

        buttonLayout.add(downloadAnchor, resetButton);

        verticalLayout.add(settingsLayout, carsLayout, buttonLayout, previewTextArea);
        return verticalLayout;
    }

    private StreamResource downloadBop() {
        return new StreamResource(
                "bop.json",
                () -> {
                    try {
                        return new ByteArrayInputStream(JsonUtils.toJson(currentBop).getBytes(StandardCharsets.UTF_8));
                    } catch (JsonProcessingException e) {
                        String errorMessage = "Failed to create download resource for BoP file";
                        notificationService.showErrorNotification(errorMessage);
                        log.severe(errorMessage + TEXT_DELIMITER + e.getMessage());
                        return null;
                    }
                }
        );
    }

    private void resetBop() {
        currentBop = new AccBop();
        currentCarComponents.clear();
        carsComboBox.clear();
        trackComboBox.clear();
        reloadComponents();
    }

    private void reloadComponents() {
        try {
            previewTextArea.setValue(JsonUtils.toJsonPretty(currentBop));
            carsLayout.removeAll();
            carsLayout.add(currentCarComponents.values());
        } catch (JsonProcessingException e) {
            String errorMessage = "Failed to update preview";
            notificationService.showErrorNotification(errorMessage);
            log.severe(errorMessage + TEXT_DELIMITER + e.getMessage());
        }
    }
}
