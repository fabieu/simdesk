package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.entrylist.Entrylist;
import de.sustineo.acc.servertools.entities.validation.ValidationData;
import de.sustineo.acc.servertools.entities.validation.ValidationError;
import de.sustineo.acc.servertools.entities.validation.ValidationRule;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.NotificationService;
import de.sustineo.acc.servertools.services.ValidationService;
import de.sustineo.acc.servertools.services.entrylist.EntrylistService;
import de.sustineo.acc.servertools.utils.json.JsonUtils;
import de.sustineo.acc.servertools.views.i18n.UploadI18NDefaults;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;

@Profile(ProfileManager.PROFILE_ENTRYLIST)
@Log
@Route(value = "/entrylist/validator", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "Entrylist - Validator")
@AnonymousAllowed
public class EntrylistValidatorView extends VerticalLayout {
    private static final String NOTIFICATION_DELIMITER = " - ";
    private static final Duration NOTIFICATION_DURATION = Duration.ofSeconds(10);

    private final EntrylistService entrylistService;
    private final JsonUtils jsonUtils;
    private final ValidationService validationService;
    private final NotificationService notificationService;

    public EntrylistValidatorView(EntrylistService entrylistService, JsonUtils jsonUtils, Validator validator, ValidationService validationService, NotificationService notificationService) {
        this.entrylistService = entrylistService;
        this.jsonUtils = jsonUtils;
        this.validationService = validationService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);

        addAndExpand(createEntrylistValidationForm());
    }

    private Component createEntrylistValidationForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        /* Validation rules start */
        VerticalLayout validationRulesLayout = new VerticalLayout();
        validationRulesLayout.setSpacing(false);

        H4 validationRulesTitle = new H4("1. Choose validation rules");
        CheckboxGroup<String> validationRulesCheckboxGroup = new CheckboxGroup<>();
        validationRulesCheckboxGroup.setItems(ValidationRule.getFriendlyNames());
        validationRulesCheckboxGroup.select(ValidationRule.getFriendlyNames());
        validationRulesCheckboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        add(validationRulesCheckboxGroup);

        validationRulesLayout.add(validationRulesTitle, validationRulesCheckboxGroup);
        /* Validation rules end */

        /* File upload start */
        VerticalLayout fileUploadLayout = new VerticalLayout();
        fileUploadLayout.setSpacing(false);

        H4 fileUploadTitle = new H4("2. Upload one or more entrylist files");
        Paragraph fileUploadHint = new Paragraph("File size must be less than or equal to 1 MB. Only valid JSON files are accepted.");
        fileUploadHint.getStyle().setColor("var(--lumo-secondary-text-color)");
        Paragraph fileUploadExplanation = new Paragraph("The entrylist files are validated against the selected validation rules during upload. The results of the validation are displayed immediately.");

        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
        Upload fileUpload = new Upload(multiFileMemoryBuffer);
        fileUpload.setWidthFull();
        fileUpload.setDropAllowed(true);
        fileUpload.setAcceptedFileTypes("application/json", ".json");
        fileUpload.setMaxFileSize(1024 * 1024 * 1); // 1 MB
        fileUpload.setI18n(configureUploadI18N());
        fileUpload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream fileData = multiFileMemoryBuffer.getInputStream(fileName);

            try {
                Entrylist entrylist = jsonUtils.fromJson(fileData, Entrylist.class);
                validationService.validate(entrylist);
                ValidationData validationData = entrylistService.validateRules(entrylist, ValidationRule.fromFriendlyNames(validationRulesCheckboxGroup.getSelectedItems()));

                if (validationData.getErrors().isEmpty()) {
                    createValidationSuccessNotification(fileName);
                } else {
                    for (ValidationError validationError : validationData.getErrors()) {
                        createValidationErrorNotification(fileName, validationError);
                    }
                }
            } catch (ConstraintViolationException e) {
                throw new RuntimeException("Invalid entrylist file" + NOTIFICATION_DELIMITER + e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException("Invalid JSON file", e);
            }
        });
        fileUpload.addFileRejectedListener(event -> {
            notificationService.showErrorNotification(event.getErrorMessage(), NOTIFICATION_DURATION);
        });
        fileUpload.addFailedListener(event -> {
            notificationService.showErrorNotification(event.getFileName() + NOTIFICATION_DELIMITER + event.getReason().getMessage(), NOTIFICATION_DURATION);
        });

        fileUploadLayout.add(fileUploadTitle, fileUploadHint, fileUpload, fileUploadExplanation);
        /* File upload end */

        layout.add(validationRulesLayout, fileUploadLayout, createValidationRuleDetailsLayout());
        return layout;
    }

    private Component createValidationRuleDetailsLayout() {
        VerticalLayout layout = new VerticalLayout();

        H4 title = new H4("Validation rule details");

        Accordion accordion = new Accordion();
        accordion.setWidthFull();

        for (ValidationRule validationRule : ValidationRule.values()) {
            AccordionPanel accordionPanel = accordion.add(validationRule.getFriendlyName(), new Text(validationRule.getDescription()));
            accordionPanel.addThemeVariants(DetailsVariant.FILLED);
            accordionPanel.setOpened(false);
        }

        layout.add(title, accordion);

        return layout;
    }

    private UploadI18N configureUploadI18N() {
        UploadI18NDefaults i18n = new UploadI18NDefaults();

        i18n.getAddFiles().setOne("Upload entrylist.json file...");
        i18n.getAddFiles().setMany("Upload entrylist.json files...");
        i18n.getDropFiles().setOne("Drop entrylist.json file here");
        i18n.getDropFiles().setMany("Drop entrylist.json files here");
        i18n.getError().setIncorrectFileType("The provided file does not have the correct format (JSON).");
        i18n.getError().setFileIsTooBig("The provided file is too big. Maximum file size is 1 MB.");

        return i18n;
    }

    private void createValidationSuccessNotification(String fileName) {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.BOTTOM_STRETCH);
        notification.setDuration((int) NOTIFICATION_DURATION.toMillis());
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        Icon icon = VaadinIcon.CHECK_CIRCLE_O.create();
        Div messageContainer = new Div(new Text(fileName + NOTIFICATION_DELIMITER + "Validation passed"));

        HorizontalLayout layout = new HorizontalLayout(icon, messageContainer, createCloseButton(notification));
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    private void createValidationErrorNotification(String fileName, ValidationError validationError) {
        List<Object> errorReferences = validationError.getReferences();
        ValidationRule validationRule = validationError.getRule();

        Notification notification = new Notification();
        notification.setPosition(Notification.Position.BOTTOM_STRETCH);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(0);

        Icon icon = VaadinIcon.WARNING.create();
        Div messageContainer = new Div(new Text(fileName + NOTIFICATION_DELIMITER + validationRule.getFriendlyName() + NOTIFICATION_DELIMITER + validationError.getMessage()));

        // Add dynamic dialog with error references if available
        if (errorReferences != null && !errorReferences.isEmpty()) {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle(validationRule.getFriendlyName() + NOTIFICATION_DELIMITER + validationError.getMessage());

            VerticalLayout dialogLayout = new VerticalLayout();

            for (Object reference : errorReferences) {
                Div referenceContainer = new Div(new Text(reference.toString()));
                dialogLayout.add(referenceContainer);
            }

            dialog.add(dialogLayout);

            Button dialogButton = new Button("View details", clickEvent -> dialog.open());
            dialogButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            dialogButton.getStyle()
                    .set("margin-left", "10px");
            messageContainer.add(dialogButton);
        }

        HorizontalLayout layout = new HorizontalLayout(icon, messageContainer, createCloseButton(notification));
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    private static Button createCloseButton(Notification notification) {
        Button closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), clickEvent -> notification.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        return closeButton;
    }
}
