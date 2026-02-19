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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.stewarding.PenaltyCatalogService;
import de.sustineo.simdesk.views.BaseView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/catalogs", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "STEWARD"})
public class PenaltyCatalogListView extends BaseView {
    private final PenaltyCatalogService catalogService;

    public PenaltyCatalogListView(PenaltyCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public String getPageTitle() {
        return "Penalty Catalogs";
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

        Button newButton = new Button("New Catalog", e -> openNewCatalogDialog());
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        headerLayout.add(newButton);

        add(headerLayout);

        List<PenaltyCatalog> catalogs = catalogService.getAllCatalogs();
        Grid<PenaltyCatalog> grid = new Grid<>(PenaltyCatalog.class, false);
        grid.addColumn(PenaltyCatalog::getName).setHeader("Name").setSortable(true);
        grid.addColumn(PenaltyCatalog::getDescription).setHeader("Description").setSortable(true);
        grid.setItems(catalogs);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(PenaltyCatalogDetailView.class,
                        new RouteParameters("catalogId", String.valueOf(e.getItem().getId()))))
        );

        addAndExpand(grid);
    }

    private void openNewCatalogDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Penalty Catalog");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        nameField.setWidthFull();

        TextField descriptionField = new TextField("Description");
        descriptionField.setWidthFull();

        form.add(nameField, descriptionField);

        Button saveButton = new Button("Save", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Name is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            PenaltyCatalog catalog = PenaltyCatalog.builder()
                    .name(nameField.getValue())
                    .description(descriptionField.getValue())
                    .build();
            catalogService.createCatalog(catalog);
            dialog.close();
            Notification.show("Catalog created", 3000, Notification.Position.MIDDLE)
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
