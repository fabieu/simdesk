package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.UserService;
import lombok.extern.java.Log;

@Log
@Route(value = "/settings", layout = MainLayout.class)
@PageTitle("Settings")
//@RolesAllowed({"ADMIN"})
@AnonymousAllowed
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
        tabSheet.setWidthFull();
        tabSheet.add("General", createGeneralTab());
        tabSheet.add("Discord", createDiscordTab());
        return tabSheet;
    }

    private Component createGeneralTab() {
        VerticalLayout layout = new VerticalLayout();

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
}
