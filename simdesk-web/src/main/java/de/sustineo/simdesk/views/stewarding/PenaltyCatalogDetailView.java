package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.entities.stewarding.PenaltyDefinition;
import de.sustineo.simdesk.entities.stewarding.PenaltySessionType;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.stewarding.PenaltyCatalogService;
import de.sustineo.simdesk.views.BaseView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/catalogs/:catalogId", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "STEWARD"})
public class PenaltyCatalogDetailView extends BaseView {
    private final PenaltyCatalogService catalogService;

    public PenaltyCatalogDetailView(PenaltyCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public String getPageTitle() {
        return "Penalty Catalog Details";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        String catalogIdParam = event.getRouteParameters().get("catalogId").orElse(null);
        if (catalogIdParam == null) {
            getUI().ifPresent(ui -> ui.navigate(PenaltyCatalogListView.class));
            return;
        }

        Integer catalogId;
        try {
            catalogId = Integer.valueOf(catalogIdParam);
        } catch (NumberFormatException e) {
            getUI().ifPresent(ui -> ui.navigate(PenaltyCatalogListView.class));
            return;
        }

        PenaltyCatalog catalog = catalogService.getCatalogById(catalogId);
        if (catalog == null) {
            getUI().ifPresent(ui -> ui.navigate(PenaltyCatalogListView.class));
            return;
        }

        add(createViewHeader(catalog.getName()));

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(false);
        if (catalog.getDescription() != null && !catalog.getDescription().isEmpty()) {
            infoLayout.add(new Paragraph(catalog.getDescription()));
        }
        add(infoLayout);

        HorizontalLayout actionLayout = new HorizontalLayout();
        Button addPenaltyButton = new Button("Add Penalty", e -> openPenaltyDialog(catalogId));
        addPenaltyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        actionLayout.add(addPenaltyButton);
        add(actionLayout);

        List<PenaltyDefinition> definitions = catalogService.getDefinitionsByCatalogId(catalogId);
        Grid<PenaltyDefinition> grid = new Grid<>(PenaltyDefinition.class, false);
        grid.addColumn(PenaltyDefinition::getCode).setHeader("Code").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(PenaltyDefinition::getName).setHeader("Name").setSortable(true);
        grid.addColumn(PenaltyDefinition::getCategory).setHeader("Category").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(def -> def.getSessionType() != null ? def.getSessionType().getDescription() : "-")
                .setHeader("Session Type").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(PenaltyDefinition::getDefaultPenalty).setHeader("Default Penalty").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(PenaltyDefinition::getSeverity).setHeader("Severity").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.setItems(definitions);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        addAndExpand(grid);
    }

    private void openPenaltyDialog(Integer catalogId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add Penalty Definition");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        TextField codeField = new TextField("Code");
        codeField.setRequired(true);

        TextField nameField = new TextField("Name");
        nameField.setRequired(true);

        TextField categoryField = new TextField("Category");

        ComboBox<PenaltySessionType> sessionTypeCombo = new ComboBox<>("Session Type");
        sessionTypeCombo.setItems(PenaltySessionType.values());
        sessionTypeCombo.setItemLabelGenerator(PenaltySessionType::getDescription);

        TextField defaultPenaltyField = new TextField("Default Penalty");

        IntegerField severityField = new IntegerField("Severity");
        severityField.setMin(0);

        IntegerField sortOrderField = new IntegerField("Sort Order");
        sortOrderField.setMin(0);

        form.add(codeField, nameField, categoryField, sessionTypeCombo, defaultPenaltyField, severityField, sortOrderField);

        Button saveButton = new Button("Save", e -> {
            if (codeField.isEmpty() || nameField.isEmpty()) {
                Notification.show("Code and Name are required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            PenaltyDefinition definition = PenaltyDefinition.builder()
                    .catalogId(catalogId)
                    .code(codeField.getValue())
                    .name(nameField.getValue())
                    .category(categoryField.getValue())
                    .sessionType(sessionTypeCombo.getValue())
                    .defaultPenalty(defaultPenaltyField.getValue())
                    .severity(severityField.getValue())
                    .sortOrder(sortOrderField.getValue())
                    .build();

            catalogService.createDefinition(definition);
            dialog.close();
            Notification.show("Penalty definition added", 3000, Notification.Position.MIDDLE)
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
