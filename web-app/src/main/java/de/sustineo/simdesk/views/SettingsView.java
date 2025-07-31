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
import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.ApiKeyService;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.auth.UserService;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.components.ButtonComponentFactory;
import de.sustineo.simdesk.views.filter.DriverFilter;
import de.sustineo.simdesk.views.filter.GridFilter;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

@Log
@Route(value = "/settings", layout = MainLayout.class)
@PageTitle("Settings")
@RolesAllowed({"ADMIN"})
public class SettingsView extends BaseView {
    private final UserService userService;
    private final NotificationService notificationService;
    private final DriverService driverService;
    private final ApiKeyService apiKeyService;
    private final SecurityService securityService;

    private final ButtonComponentFactory buttonComponentFactory;

    private final Grid<ApiKey> grid = new Grid<>(ApiKey.class, false);

    public SettingsView(NotificationService notificationService,
                        UserService userService,
                        DriverService driverService,
                        ApiKeyService apiKeyService,
                        SecurityService securityService,
                        ButtonComponentFactory buttonComponentFactory) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.driverService = driverService;
        this.apiKeyService = apiKeyService;
        this.securityService = securityService;
        this.buttonComponentFactory = buttonComponentFactory;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createFormLayout());
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
        tabSheet.add("API Keys", createApiKeysTab());

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

    private Component createApiKeysTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.add(createApiKeysLayout());
        return layout;
    }

    private Component createDiscordTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.add(createUserRoleLayout());
        return layout;
    }

    private Component createDriverVisibilityLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

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
                notificationService.showSuccessNotification(String.format("%s updated successfully", driver.getFullName()));
            } catch (Exception e) {
                notificationService.showErrorNotification(String.format("Failed to update %s - %s", driver.getFullName(), e.getMessage()));
            }
        });

        Grid.Column<Driver> driverIdColumn = grid.addColumn(Driver::getId)
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
        visibilityField.setItems(Visibility.PRIVATE, Visibility.PUBLIC);
        binder.forField(visibilityField)
                .bind(Driver::getVisibility, Driver::setVisibility);
        visibilityColumn.setEditorComponent(visibilityField);

        Button saveButton = buttonComponentFactory.createSuccessButton("Save");
        saveButton.addClickListener(e -> editor.save());

        Button cancelButton = buttonComponentFactory.createCancelIconButton();
        cancelButton.addClickListener(e -> editor.cancel());

        GridListDataView<Driver> dataView = grid.setItems(driverService.getAllDrivers());
        DriverFilter driverFilter = new DriverFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(driverIdColumn).setComponent(GridFilter.createHeader(driverFilter::setDriverId));
        headerRow.getCell(fullNameColumn).setComponent(GridFilter.createHeader(driverFilter::setFullName));
        headerRow.getCell(visibilityColumn).setComponent(GridFilter.createHeader(driverFilter::setVisibility));

        HorizontalLayout editActions = new HorizontalLayout(saveButton, cancelButton);
        editActions.setPadding(false);
        updateColumn.setEditorComponent(editActions);

        layout.add(createTitle("Driver Visibility"), grid);
        return layout;
    }

    private Component createApiKeysLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        layout.add(createTitle("API Keys"));

        Optional<UserPrincipal> userPrincipal = securityService.getAuthenticatedUserPrincipal();
        if (userPrincipal.isPresent()) {
            User user = userService.findByUsername(userPrincipal.get().getUsername());
            if (user != null) {
                layout.add(createApiKeyCreationLayout(user), createApiKeyGrid(user));
            }
        }

        return layout;
    }

    private void refreshApiKeyGrid(@Nonnull User user) {
        grid.setItems(apiKeyService.getByUserId(user.getId()));
        grid.getDataProvider().refreshAll();
    }

    private Component createApiKeyCreationLayout(@Nonnull User user) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(false);

        TextField nameField = new TextField();
        nameField.setWidth("15rem");
        nameField.setPlaceholder("API key name");
        nameField.setClearButtonVisible(true);

        Button createButton = buttonComponentFactory.createPrimaryButton("Generate");
        createButton.addClickListener(e -> {
            if (nameField.getValue() == null || nameField.getValue().isEmpty()) {
                notificationService.showErrorNotification("API key name is mandatory");
                return;
            }

            apiKeyService.create(user.getId(), nameField.getValue());
            refreshApiKeyGrid(user);
        });

        layout.add(nameField, createButton);
        return layout;
    }

    private Component createApiKeyGrid(@Nonnull User user) {
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setEmptyStateText("No API keys configured");
        grid.setAllRowsVisible(true);

        refreshApiKeyGrid(user);

        Grid.Column<ApiKey> apiKeyColumn = grid.addColumn(ApiKey::getApiKey)
                .setHeader("Key")
                .setTooltipGenerator(ApiKey::getApiKey);
        Grid.Column<ApiKey> nameColumn = grid.addColumn(ApiKey::getName)
                .setHeader("Name")
                .setTooltipGenerator(ApiKey::getName);
        Grid.Column<ApiKey> activeColumn = grid.addColumn(apiKey -> Boolean.TRUE.equals(apiKey.getActive()) ? "Active" : "Inactive")
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<ApiKey> actionColumn = grid.addComponentColumn(apiKey -> createApiKeyActionComponent(apiKey, user))
                .setHeader("Actions")
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("170px")
                .setFlexGrow(0);

        return grid;
    }

    private Component createApiKeyActionComponent(@Nonnull ApiKey apiKey, @Nonnull User user) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(JustifyContentMode.END);

        Button deleteButton = new Button(VaadinIcon.CLOSE.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            apiKeyService.deleteApiKey(apiKey);
            refreshApiKeyGrid(user);
        });

        layout.add(deleteButton);
        return layout;
    }

    private Component createUserRoleLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

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
                    Button updateButton = buttonComponentFactory.createPrimaryButton("Update");
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

        Button saveButton = buttonComponentFactory.createSuccessButton("Save");
        saveButton.addClickListener(e -> editor.save());

        Button cancelButton = buttonComponentFactory.createCancelIconButton();
        cancelButton.addClickListener(e -> editor.cancel());

        HorizontalLayout editActions = new HorizontalLayout(saveButton, cancelButton);
        editActions.setPadding(false);
        updateColumn.setEditorComponent(editActions);

        layout.add(createTitle("User Roles"), grid);
        return layout;
    }

    private H3 createTitle(String title) {
        return new H3(title);
    }
}
