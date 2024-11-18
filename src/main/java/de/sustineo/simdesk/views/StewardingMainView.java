package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Simulation;
import de.sustineo.simdesk.entities.stewarding.StewardingEvent;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.stewarding.StewardingService;
import de.sustineo.simdesk.utils.FormatUtils;
import lombok.extern.java.Log;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log
@Profile(ProfileManager.PROFILE_STEWARDING)
@Route(value = "/stewarding", layout = MainLayout.class)
@PageTitle("Stewarding Management")
//@RolesAllowed({"ADMIN", "HEAD_STEWARD"})
@AnonymousAllowed
public class StewardingMainView extends BaseView {
    private final StewardingService stewardingService;
    private final NotificationService notificationService;

    private final Div eventLayout = new Div();
    private final Map<Integer, FlexLayout> eventLayoutsMap = new LinkedHashMap<>();

    public StewardingMainView(StewardingService stewardingService,
                              NotificationService notificationService) {
        this.stewardingService = stewardingService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createFormLayout());
        add(createFooter());
    }

    private Component createFormLayout() {
        Div layout = new Div();
        layout.addClassNames("container");
        layout.add(createActionHeader(), createEventLayouts());
        return layout;
    }

    private Component createActionHeader() {
        FlexLayout layout = new FlexLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.END);
        layout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-s)")
                .setMarginBottom("var(--lumo-space-m)");

        Button createEventButton = new Button("Create new event");
        createEventButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        createEventButton.addClickListener(event -> createEventCreateDialog().open());

        Button penaltySetNavigationButton = new Button("Penalty sets");
        penaltySetNavigationButton.addClickListener(event -> penaltySetNavigationButton.getUI().ifPresent(ui -> ui.navigate("penalty-sets")));

        layout.add(createEventButton, penaltySetNavigationButton);
        return layout;
    }

    private Component createEventLayouts() {
        List<StewardingEvent> events = stewardingService.getAllActiveEvents();
        for (StewardingEvent event : events) {
            FlexLayout eventPanel = createEventPanel(event);

            eventLayout.add(eventPanel);
            eventLayoutsMap.put(event.getId(), eventPanel);
        }

        return eventLayout;
    }

    private FlexLayout createEventPanel(StewardingEvent stewardingEvent) {
        FlexLayout eventPanel = new FlexLayout();
        eventPanel.setAlignItems(Alignment.CENTER);
        eventPanel.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .setPadding("var(--lumo-space-m)")
                .setMarginBottom("var(--lumo-space-m)")
                .set("gap", "var(--lumo-space-s)")
                .setBorder("1px solid var(--lumo-contrast-10pct)")
                .setBorderRadius("var(--lumo-border-radius-m)");

        H3 title = new H3(stewardingEvent.getName());

        Span simulation = new Span(EnumUtils.getEnum(Simulation.class, stewardingEvent.getSimulationId()).getShortName());
        simulation.getElement().getThemeList().add("badge");

        Span datetime = new Span(FormatUtils.formatDatetime(stewardingEvent.getStartDatetime()) + " - " + FormatUtils.formatDatetime(stewardingEvent.getEndDatetime()));
        datetime.getElement().getThemeList().add("badge contrast");
        datetime.getStyle().setMarginRight("auto");

        Button editEventButton = new Button("Edit");
        editEventButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_PRIMARY);
        editEventButton.setAriaLabel("Edit event");
        editEventButton.addClickListener(event -> editEvent(stewardingEvent));

        Button deleteEventButton = new Button(new Icon(VaadinIcon.CLOSE));
        deleteEventButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        deleteEventButton.setAriaLabel("Remove event");
        deleteEventButton.addClickListener(event -> createEventDeleteDialog(stewardingEvent).open());

        eventPanel.add(title, simulation, datetime, editEventButton, deleteEventButton);
        return eventPanel;
    }

    private void editEvent(StewardingEvent stewardingEvent) {
    }

    private Dialog createEventCreateDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create new event");

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelButton.getStyle()
                .set("margin-right", "auto");

        Button createButton = new Button("Create");
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        createButton.addClickListener(event -> {
            StewardingEvent stewardingEvent = new StewardingEvent();
            stewardingService.saveEvent(stewardingEvent);

            dialog.close();
            notificationService.showSuccessNotification("Event successfully created");
        });

        dialog.getFooter().add(cancelButton, createButton);

        dialog.add(new Text("Form goes here"));
        return dialog;
    }

    private Dialog createEventDeleteDialog(StewardingEvent stewardingEvent) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Delete event - " + stewardingEvent.getName());

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelButton.getStyle()
                .set("margin-right", "auto");

        Button deleteButton = new Button("Delete");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> {
            stewardingService.archiveEvent(stewardingEvent);

            eventLayout.remove(eventLayoutsMap.get(stewardingEvent.getId()));
            eventLayoutsMap.remove(stewardingEvent.getId());

            dialog.close();
            notificationService.showSuccessNotification("Event successfully deleted");
        });

        dialog.getFooter().add(cancelButton, deleteButton);

        dialog.add(new Text("Are you sure you want to delete this event?"));
        return dialog;
    }
}
