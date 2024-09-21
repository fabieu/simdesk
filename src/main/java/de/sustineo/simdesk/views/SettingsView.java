package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.UserService;
import lombok.extern.java.Log;

@Log
@Route(value = "/settings", layout = MainLayout.class)
@PageTitle("Settings")
//@RolesAllowed({"ADMIN"})
@AnonymousAllowed
public class SettingsView extends BaseView {
    private final UserService userService;

    public SettingsView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createSettingsPanel());
        add(createFooter());
    }

    private Component createSettingsPanel() {
        VerticalLayout verticalLayout = new VerticalLayout();
        return verticalLayout;
    }
}
