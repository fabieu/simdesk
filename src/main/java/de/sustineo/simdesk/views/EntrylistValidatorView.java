package de.sustineo.simdesk.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.entities.entrylist.Entrylist;
import de.sustineo.simdesk.entities.validation.ValidationData;
import de.sustineo.simdesk.entities.validation.ValidationError;
import de.sustineo.simdesk.entities.validation.ValidationRule;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.ValidationService;
import de.sustineo.simdesk.services.entrylist.EntrylistService;
import de.sustineo.simdesk.utils.json.JsonUtils;
import de.sustineo.simdesk.views.i18n.UploadI18NDefaults;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Profile(ProfileManager.PROFILE_ENTRYLIST)
@Log
@Route(value = "/entrylist/validator", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Entrylist - Validator")
@AnonymousAllowed
public class EntrylistValidatorView extends VerticalLayout {
    private static final String NOTIFICATION_DELIMITER = " - ";

    private final EntrylistService entrylistService;
    private final JsonUtils jsonUtils;
    private final ValidationService validationService;
    private final NotificationService notificationService;

    public EntrylistValidatorView(EntrylistService entrylistService,
                                  JsonUtils jsonUtils,
                                  ValidationService validationService,
                                  NotificationService notificationService) {
        this.entrylistService = entrylistService;
        this.jsonUtils = jsonUtils;
        this.validationService = validationService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        getStyle().setPadding("var(--lumo-space-l)");

        addAndExpand(createEntrylistValidationForm());
    }

    private Component createEntrylistValidationForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSizeFull();

        /* Validation rules start */
        VerticalLayout validationRulesLayout = new VerticalLayout();
        validationRulesLayout.setPadding(false);
        validationRulesLayout.setSpacing(false);

        H4 validationRulesTitle = new H4("1. Choose validation rules");
        CheckboxGroup<ValidationRule> validationRulesCheckboxGroup = new CheckboxGroup<>();
        validationRulesCheckboxGroup.setItems(ValidationRule.values());
        validationRulesCheckboxGroup.setItemLabelGenerator(ValidationRule::getFriendlyName);
        validationRulesCheckboxGroup.select(ValidationRule.values());
        validationRulesCheckboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        add(validationRulesCheckboxGroup);

        validationRulesLayout.add(validationRulesTitle, validationRulesCheckboxGroup);
        /* Validation rules end */

        /* File upload start */
        VerticalLayout fileUploadLayout = new VerticalLayout();
        fileUploadLayout.setPadding(false);
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
        fileUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        fileUpload.setI18n(configureUploadI18N());
        fileUpload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream fileData = multiFileMemoryBuffer.getInputStream(fileName);

            try {
                Entrylist entrylist = jsonUtils.fromJson(fileData, Entrylist.class);
                validationService.validate(entrylist);
                ValidationData validationData = entrylistService.validateRules(entrylist, new ArrayList<>(validationRulesCheckboxGroup.getSelectedItems()));

                if (validationData.getErrors().isEmpty()) {
                    createValidationSuccessNotification(fileName);
                } else {
                    for (ValidationError validationError : validationData.getErrors()) {
                        createValidationErrorNotification(fileName, validationError);
                    }
                }
            } catch (ConstraintViolationException e) {
                throw new RuntimeException("Invalid entrylist file", e);
            } catch (IOException e) {
                throw new RuntimeException("Invalid JSON file", e);
            }
        });
        fileUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(event.getErrorMessage()));
        fileUpload.addFailedListener(event -> notificationService.showErrorNotification(event.getFileName() + NOTIFICATION_DELIMITER + event.getReason().getMessage()));

        fileUploadLayout.add(fileUploadTitle, fileUploadHint, fileUpload, fileUploadExplanation);
        /* File upload end */

        layout.add(validationRulesLayout, fileUploadLayout, createValidationRuleDetailsLayout());
        return layout;
    }

    private Component createValidationRuleDetailsLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

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
        Div header = new Div(new Text(fileName));
        header.getStyle()
                .setFontSize("var(--lumo-font-size-m)")
                .setFontWeight(Style.FontWeight.BOLD);

        Div description = new Div(new Text("Validation passed"));
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

        Div description = new Div(new Text(validationRule.getFriendlyName() + NOTIFICATION_DELIMITER + validationError.getMessage()));
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
                String referenceRepresentation;
                try {
                    referenceRepresentation = jsonUtils.toJsonPretty(reference);
                } catch (JsonProcessingException e) {
                    referenceRepresentation = reference.toString();
                }
                Span referenceSpan = new Span(referenceRepresentation);
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
