package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveListener;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;

import java.util.Objects;

@Log
@Route(value = "/settings", layout = MainLayout.class)
@PageTitle("Settings")
@RolesAllowed({"ADMIN"})
public class SettingsView extends BaseView {
    private final UserService userService;
    private final NotificationService notificationService;

    public SettingsView(UserService userService,
                        NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;

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

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(event -> {
            notificationService.showInfoNotification("Save button clicked");
        });

        FlexLayout actionLayout = new FlexLayout(saveButton);
        actionLayout.setWidthFull();
        actionLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        actionLayout.setAlignItems(Alignment.END);

        layout.add(actionLayout);
        return layout;
    }

    private Component createDiscordTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.add(createUserRoleLayout());
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
            userService.updateUserRole(userRole);
            notificationService.showSuccessNotification(String.format("%s updated successfully", userRole.getName()));
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
                .setWidth("15rem ")
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

        Button saveButton = new Button("Save", e -> editor.save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout editActions = new HorizontalLayout(saveButton, cancelButton);
        editActions.setPadding(false);
        updateColumn.setEditorComponent(editActions);

        layout.add(title, grid);
        return layout;
    }
}
