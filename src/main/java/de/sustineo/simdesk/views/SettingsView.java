package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveListener;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.UserService;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.filter.DriverFilter;
import de.sustineo.simdesk.views.filter.GridFilter;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Log
@Route(value = "/settings", layout = MainLayout.class)
@PageTitle("Settings")
@RolesAllowed({"ADMIN"})
public class SettingsView extends BaseView {
    private final UserService userService;
    private final NotificationService notificationService;
    private final DriverService driverService;

    public SettingsView(NotificationService notificationService,
                        UserService userService,
                        DriverService driverService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.driverService = driverService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createFormLayout());
        add(createFooter());
    }

    private Component createFormLayout() {
        FlexLayout layout = new FlexLayout();
        layout.addClassNames("container", "bg-light");
        layout.add(createTabSheet());
        return layout;
    }

    private Component createTabSheet() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.add("General", createGeneralTab());

        if (ProfileManager.isDiscordProfileEnabled()) {
            tabSheet.add("Discord", createDiscordTab());
        }

        return tabSheet;
    }

    private Component createGeneralTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.add(createDriverVisibilityLayout());
        return layout;
    }

    private Component createDiscordTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.add(createUserRoleLayout());
        return layout;
    }

    private Component createDriverVisibilityLayout() {
        List<Driver> drivers = driverService.getAllDrivers();

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        H3 title = new H3("Driver Visibility");

        Grid<Driver> grid = new Grid<>(Driver.class, false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        Binder<Driver> binder = new Binder<>(Driver.class);
        Editor<Driver> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener((EditorSaveListener<Driver>) event -> {
            Driver driver = event.getItem();
            try {
                driverService.updateDriverVisibility(driver);
                notificationService.showSuccessNotification(String.format("%s updated successfully", driver.getFullNameCensored()));
            } catch (Exception e) {
                notificationService.showErrorNotification(String.format("Failed to update %s - %s", driver.getFullNameCensored(), e.getMessage()));
            }
        });

        Grid.Column<Driver> playerIdColumn = grid.addColumn(Driver::getPlayerId)
                .setHeader("Steam ID")
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<Driver> fullNameColumn = grid.addColumn(Driver::getFullName)
                .setHeader("Name")
                .setTooltipGenerator(Driver::getFullName);
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
                    Button updateButton = new Button("Update");
                    updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
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
        visibilityField.setItems(Visibility.values());
        binder.forField(visibilityField)
                .bind(Driver::getVisibility, Driver::setVisibility);
        visibilityColumn.setEditorComponent(visibilityField);

        Button saveButton = createSaveButton();
        saveButton.addClickListener(e -> editor.save());

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        GridListDataView<Driver> dataView = grid.setItems(drivers);
        DriverFilter driverFilter = new DriverFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(playerIdColumn).setComponent(GridFilter.createHeader(driverFilter::setPlayerId));
        headerRow.getCell(fullNameColumn).setComponent(GridFilter.createHeader(driverFilter::setFullName));
        headerRow.getCell(visibilityColumn).setComponent(GridFilter.createHeader(driverFilter::setVisibility));

        HorizontalLayout editActions = new HorizontalLayout(saveButton, cancelButton);
        editActions.setPadding(false);
        updateColumn.setEditorComponent(editActions);

        layout.add(title, grid);
        return layout;
    }

    private Component createUserRoleLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        H3 title = new H3("User Roles");

        Grid<UserRole> grid = new Grid<>(UserRole.class, false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setItems(userService.getAllRoles());
        grid.setAllRowsVisible(true);

        Editor<UserRole> editor = grid.getEditor();
        Binder<UserRole> binder = new Binder<>(UserRole.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener((EditorSaveListener<UserRole>) event -> {
            UserRole userRole = event.getItem();
            try {
                userService.updateUserRole(userRole);
                notificationService.showSuccessNotification(String.format("%s updated successfully", userRole.getName()));
            } catch (Exception e) {
                notificationService.showErrorNotification(String.format("Failed to update %s - %s", userRole.getName(), e.getMessage()));
            }
        });

        Grid.Column<UserRole> roleNameColumn = grid.addColumn(UserRole::getName)
                .setHeader("Role")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<UserRole> descriptionColumn = grid.addColumn(UserRole::getDescription)
                .setHeader("Description");
        Grid.Column<UserRole> discordRoleIdColumn = grid.addColumn(UserRole::getDiscordRoleId)
                .setHeader("Discord Role ID")
                .setWidth("15rem")
                .setFlexGrow(0);
        Grid.Column<UserRole> updateColumn = grid.addComponentColumn(userRole -> {
                    Button updateButton = new Button("Update");
                    updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
                    updateButton.addClickListener(e -> {
                        if (editor.isOpen()) {
                            editor.cancel();
                        }
                        editor.editItem(userRole);
                    });
                    return updateButton;
                })
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("170px")
                .setFlexGrow(0);

        TextField discordRoleIdField = new TextField();
        discordRoleIdField.setWidthFull();
        discordRoleIdField.setClearButtonVisible(true);
        binder.forField(discordRoleIdField)
                .withNullRepresentation("")
                .withConverter(s -> s, s -> Objects.toString(s, ""))
                .withValidator(discordId -> discordId == null || discordId.matches("[0-9]+"), "Discord Role ID is not a valid")
                .bind(UserRole::getDiscordRoleId, UserRole::setDiscordRoleId);
        discordRoleIdColumn.setEditorComponent(discordRoleIdField);

        Button saveButton = createSaveButton();
        saveButton.addClickListener(e -> editor.save());

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout editActions = new HorizontalLayout(saveButton, cancelButton);
        editActions.setPadding(false);
        updateColumn.setEditorComponent(editActions);

        layout.add(title, grid);
        return layout;
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        return saveButton;
    }
}
