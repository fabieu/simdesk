package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.entities.stewarding.Series;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.stewarding.PenaltyCatalogService;
import de.sustineo.simdesk.services.stewarding.SeriesService;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/series", layout = MainLayout.class)
@AnonymousAllowed
public class SeriesListView extends BaseView {
    private final SeriesService seriesService;
    private final PenaltyCatalogService catalogService;
    private final SecurityService securityService;

    public SeriesListView(SeriesService seriesService, PenaltyCatalogService catalogService,
                          SecurityService securityService) {
        this.seriesService = seriesService;
        this.catalogService = catalogService;
        this.securityService = securityService;
    }

    @Override
    public String getPageTitle() {
        return "Series";
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

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Button newButton = new Button("New Series", e -> openNewSeriesDialog());
            newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            headerLayout.add(newButton);
        }

        add(headerLayout);

        List<Series> seriesList = seriesService.getAllSeries();
        Grid<Series> grid = new Grid<>(Series.class, false);
        grid.addColumn(Series::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(Series::getStartDate).setHeader("Start Date").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(Series::getEndDate).setHeader("End Date").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.setItems(seriesList);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(SeriesDetailView.class,
                        new RouteParameters("seriesId", String.valueOf(e.getItem().getId()))))
        );

        addAndExpand(grid);
    }

    private void openNewSeriesDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Series");
        dialog.setWidth("700px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField titleField = new TextField("Title");
        titleField.setWidthFull();
        titleField.setRequired(true);

        TextArea descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();

        ComboBox<PenaltyCatalog> catalogCombo = new ComboBox<>("Penalty Catalog");
        catalogCombo.setItems(catalogService.getAllCatalogs());
        catalogCombo.setItemLabelGenerator(PenaltyCatalog::getName);
        catalogCombo.setWidthFull();

        TextField webhookField = new TextField("Discord Webhook URL");
        webhookField.setWidthFull();

        Checkbox videoUrlEnabledCheckbox = new Checkbox("Enable Video URL for incident reports");

        DatePicker startDatePicker = new DatePicker("Start Date");
        startDatePicker.setWidthFull();

        DatePicker endDatePicker = new DatePicker("End Date");
        endDatePicker.setWidthFull();

        form.add(titleField, 2);
        form.add(descriptionField, 2);
        form.add(catalogCombo, 2);
        form.add(webhookField, 2);
        form.add(videoUrlEnabledCheckbox, 2);
        form.add(startDatePicker);
        form.add(endDatePicker);

        Button saveButton = new Button("Save", e -> {
            if (titleField.isEmpty()) {
                Notification.show("Title is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Series series = Series.builder()
                    .title(titleField.getValue())
                    .description(descriptionField.getValue())
                    .penaltyCatalogId(catalogCombo.getValue() != null ? catalogCombo.getValue().getId() : null)
                    .discordWebhookUrl(webhookField.getValue())
                    .videoUrlEnabled(videoUrlEnabledCheckbox.getValue())
                    .startDate(startDatePicker.getValue())
                    .endDate(endDatePicker.getValue())
                    .build();

            seriesService.createSeries(series);
            dialog.close();
            Notification.show("Series created", 3000, Notification.Position.MIDDLE)
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
