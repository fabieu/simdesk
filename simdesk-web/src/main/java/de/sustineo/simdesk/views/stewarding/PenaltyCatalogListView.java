package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
@RolesAllowed({"ADMIN"})
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

        Button newButton = new Button("New Catalog", e ->
                getUI().ifPresent(ui -> ui.navigate(PenaltyCatalogDetailView.class,
                        new RouteParameters("catalogId", "new"))));
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        headerLayout.add(newButton);

        add(headerLayout);

        List<PenaltyCatalog> catalogs = catalogService.getAllCatalogs();
        Grid<PenaltyCatalog> grid = new Grid<>(PenaltyCatalog.class, false);
        grid.addColumn(PenaltyCatalog::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(PenaltyCatalog::getDescription).setHeader("Description").setAutoWidth(true);
        grid.setItems(catalogs);
        grid.setSizeFull();
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(PenaltyCatalogDetailView.class,
                        new RouteParameters("catalogId", String.valueOf(e.getItem().getId()))))
        );

        addAndExpand(grid);
    }
}
