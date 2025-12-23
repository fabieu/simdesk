package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveListener;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.DriverAlias;
import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.leaderboard.DriverAliasService;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.components.ButtonComponentFactory;
import de.sustineo.simdesk.views.filter.grid.DriverFilter;
import de.sustineo.simdesk.views.filter.grid.GridFilter;
import de.sustineo.simdesk.views.renderers.DriverRenderer;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.util.Comparator;
import java.util.List;

@Log
@Profile(SpringProfile.LEADERBOARD)
@Route(value = "/leaderboard/drivers")
@RolesAllowed({"ADMIN"})
@RequiredArgsConstructor
public class LeaderboardDriversView extends BaseView {
    private final DriverService driverService;
    private final DriverAliasService driverAliasService;
    private final ButtonComponentFactory buttonComponentFactory;
    private final NotificationService notificationService;

    @Override
    public String getPageTitle() {
        return "Leaderboard - Drivers";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        removeAll();

        add(createViewHeader());
        addAndExpand(createDriverLayout());
    }

    private Component createDriverLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        layout.add(createDriverGrid());
        return layout;
    }

    private Component createDriverGrid() {
        Grid<Driver> grid = new Grid<>(Driver.class, false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        Binder<Driver> binder = new Binder<>(Driver.class);
        Editor<Driver> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener((EditorSaveListener<Driver>) event -> {
            Driver driver = event.getItem();
            try {
                driverService.updateDriverVisibility(driver);
                notificationService.showSuccessNotification(String.format("%s updated successfully", driver.getRealName()));
            } catch (Exception e) {
                notificationService.showErrorNotification(String.format("Failed to update %s - %s", driver.getRealName(), e.getMessage()));
            }
        });

        Grid.Column<Driver> driverIdColumn = grid.addColumn(Driver::getId)
                .setHeader("Steam ID")
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<Driver> realNameColumn = grid.addColumn(DriverRenderer.createRealDriverRenderer())
                .setHeader("Full Name")
                .setSortable(true);
        Grid.Column<Driver> aliasesColumn = grid.addComponentColumn(driver -> {
                    if (driver.getVisibility() == Visibility.PRIVATE) {
                        return new Span();
                    }

                    List<DriverAlias> aliases = driverAliasService.getLatestAliasesByDriverId(driver.getId(), 3);
                    if (aliases.isEmpty()) {
                        return new Span();
                    }

                    HorizontalLayout aliasesLayout = new HorizontalLayout();
                    for (DriverAlias alias : aliases) {
                        Span aliasBadge = new Span(alias.getFullName());
                        aliasBadge.getElement().getThemeList().add("badge contrast");
                        aliasesLayout.add(aliasBadge);
                    }

                    return aliasesLayout;
                })
                .setHeader("Known Aliases")
                .setAutoWidth(true)
                .setFlexGrow(1);
        Grid.Column<Driver> shortNameColumn = grid.addColumn(Driver::getShortName)
                .setHeader("Short Name")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> visibilityColumn = grid.addColumn(driver -> driver.getVisibility().name())
                .setHeader("Visibility")
                .setWidth("10rem")
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> lastActivityColumn = grid.addColumn(driver -> FormatUtils.formatDatetime(driver.getLastActivity()))
                .setHeader("Last Activity")
                .setComparator(Comparator.comparing(Driver::getLastActivity))
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Driver> updateColumn = grid.addComponentColumn(driver -> {
                    Button updateButton = buttonComponentFactory.createPrimaryButton("Update");
                    updateButton.addClickListener(e -> {
                        if (editor.isOpen()) {
                            editor.cancel();
                        }
                        editor.editItem(driver);
                    });
                    return updateButton;
                })
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("170px")
                .setFlexGrow(0);

        Select<Visibility> visibilityField = new Select<>();
        visibilityField.setWidthFull();
        visibilityField.setItems(Visibility.getAll());
        binder.forField(visibilityField)
                .bind(Driver::getVisibility, Driver::setVisibility);
        visibilityColumn.setEditorComponent(visibilityField);

        Button saveButton = buttonComponentFactory.createPrimarySuccessButton("Save");
        saveButton.addClickListener(e -> editor.save());

        Button cancelButton = buttonComponentFactory.createCancelIconButton();
        cancelButton.addClickListener(e -> editor.cancel());

        GridListDataView<Driver> dataView = grid.setItems(driverService.getAllDrivers());
        DriverFilter driverFilter = new DriverFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(driverIdColumn).setComponent(GridFilter.createTextFieldHeader(driverFilter::setDriverId));
        headerRow.getCell(realNameColumn).setComponent(GridFilter.createTextFieldHeader(driverFilter::setRealName));
        headerRow.getCell(shortNameColumn).setComponent(GridFilter.createTextFieldHeader(driverFilter::setShortName));
        headerRow.getCell(visibilityColumn).setComponent(GridFilter.createComboBoxHeader(driverFilter::setVisibility, Visibility::getAll));

        HorizontalLayout editActions = new HorizontalLayout(saveButton, cancelButton);
        editActions.setPadding(false);
        updateColumn.setEditorComponent(editActions);

        return grid;
    }
}
