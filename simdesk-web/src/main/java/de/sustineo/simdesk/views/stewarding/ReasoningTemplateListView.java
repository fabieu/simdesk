package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.ReasoningTemplate;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.stewarding.ReasoningTemplateService;
import de.sustineo.simdesk.views.BaseView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/templates", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "STEWARD"})
public class ReasoningTemplateListView extends BaseView {
    private final ReasoningTemplateService templateService;

    public ReasoningTemplateListView(ReasoningTemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
    public String getPageTitle() {
        return "Reasoning Templates";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.add(createViewHeader());

        Button newButton = new Button("New Template", e -> openTemplateDialog());
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        headerLayout.add(newButton);

        add(headerLayout);

        List<ReasoningTemplate> templates = templateService.getAllTemplates();
        Grid<ReasoningTemplate> grid = new Grid<>(ReasoningTemplate.class, false);
        grid.addColumn(ReasoningTemplate::getName).setHeader("Name").setSortable(true);
        grid.addColumn(ReasoningTemplate::getCategory).setHeader("Category").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(template -> {
            String text = template.getTemplateText();
            if (text != null && text.length() > 100) {
                return text.substring(0, 100) + "...";
            }
            return text != null ? text : "-";
        }).setHeader("Template Text").setAutoWidth(true);
        grid.setItems(templates);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        addAndExpand(grid);
    }

    private void openTemplateDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Reasoning Template");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        nameField.setWidthFull();

        TextField categoryField = new TextField("Category");
        categoryField.setWidthFull();

        TextArea templateTextField = new TextArea("Template Text");
        templateTextField.setWidthFull();
        templateTextField.setMinHeight("150px");

        form.add(nameField, categoryField, templateTextField);

        Button saveButton = new Button("Save", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Name is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            ReasoningTemplate template = ReasoningTemplate.builder()
                    .name(nameField.getValue())
                    .category(categoryField.getValue())
                    .templateText(templateTextField.getValue())
                    .build();

            templateService.createTemplate(template);
            dialog.close();
            Notification.show("Template created", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
