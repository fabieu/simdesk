package de.sustineo.simdesk.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
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
import de.sustineo.simdesk.entities.entrylist.Entrylist;
import de.sustineo.simdesk.entities.entrylist.EntrylistMetadata;
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

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
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

    private Upload entrylistUpload;
    private Entrylist entrylist;
    private EntrylistMetadata entrylistMetadata;

    public EntrylistEditorView(EntrylistService entrylistService,
                               ValidationService validationService,
                               NotificationService notificationService) {
        this.entrylistService = entrylistService;
        this.validationService = validationService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createEntrylistForm());
        add(createFooter());
    }

    private Component createEntrylistForm() {
        Div layout = new Div();
        layout.addClassNames("container", "bg-light");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.add(createFileUploadLayout(), createButtonLayout());

        layout.add(formLayout);
        return layout;
    }

    private Component createFileUploadLayout() {
        VerticalLayout fileUploadLayout = new VerticalLayout();
        fileUploadLayout.setWidthFull();
        fileUploadLayout.setPadding(false);
        fileUploadLayout.setSpacing(false);

        Paragraph fileUploadHint = new Paragraph("Accepted file formats: JSON (.json). File size must be less than or equal to 1 MB.");
        fileUploadHint.getStyle().setColor("var(--lumo-secondary-text-color)");

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        entrylistUpload = new Upload(memoryBuffer);
        entrylistUpload.setWidthFull();
        entrylistUpload.setDropAllowed(true);
        entrylistUpload.setAcceptedFileTypes(MediaType.APPLICATION_JSON_VALUE);
        entrylistUpload.setMaxFileSize((int) FileUtils.ONE_MB);
        entrylistUpload.setI18n(configureUploadI18N());
        entrylistUpload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();

            try {
                entrylist = JsonUtils.fromJson(fileData, Entrylist.class);
                entrylistMetadata = EntrylistMetadata.builder()
                        .fileName(event.getFileName())
                        .type(event.getMIMEType())
                        .contentLength(event.getContentLength())
                        .build();

                // Validate entrylist file against syntax and semantic rules
                validationService.validate(entrylist);

                createValidationSuccessNotification(entrylistMetadata.getFileName(), "File uploaded successfully");
            } catch (IOException e) {
                throw new RuntimeException("Invalid JSON file", e);
            }
        });
        entrylistUpload.addFileRejectedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getErrorMessage()));
        entrylistUpload.addFailedListener(event -> notificationService.showErrorNotification(event.getReason().getMessage()));

        fileUploadLayout.add(fileUploadHint, entrylistUpload);
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

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Validation
        Dialog validationDialog = createValidationDialog();
        Button validateButton = new Button("Validate");
        validateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        validateButton.addClickListener(e -> validationDialog.open());

        // Reset
        ConfirmDialog resetDialog = createResetDialog();
        Button resetButton = new Button("Reset");
        resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetButton.addClickListener(e -> resetDialog.open());

        buttonLayout.add(validateButton, resetButton);
        return buttonLayout;
    }

    private Dialog createValidationDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Validation Rules");

        CheckboxGroup<ValidationRule> validationRulesCheckboxGroup = new CheckboxGroup<>();
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
        validationRulesCheckboxGroup.select(ValidationRule.values());
        validationRulesCheckboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        VerticalLayout validationRulesLayout = new VerticalLayout();
        validationRulesLayout.setPadding(false);
        validationRulesLayout.setSpacing(false);
        validationRulesLayout.add(validationRulesCheckboxGroup);

        dialog.add(validationRulesCheckboxGroup);

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

    private ConfirmDialog createResetDialog() {
        ConfirmDialog resetDialog = new ConfirmDialog();
        resetDialog.setHeader("Reset current entrylist");
        resetDialog.setText("Do you really want to discard the current entrylist?");
        resetDialog.setConfirmText("Reset");
        resetDialog.addConfirmListener(event -> resetEntrylist());
        resetDialog.setCancelable(true);
        return resetDialog;
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

    private void resetEntrylist() {
        entrylist = null;
        entrylistMetadata = null;
        entrylistUpload.clearFileList();
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
                String referenceRepresentation;
                try {
                    referenceRepresentation = JsonUtils.toJsonPretty(reference);
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
