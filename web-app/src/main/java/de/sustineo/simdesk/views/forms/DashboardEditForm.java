package de.sustineo.simdesk.views.forms;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import de.sustineo.simdesk.views.BrowserTimeZone;
import lombok.Getter;

import java.time.LocalDateTime;

@SuppressWarnings("FieldCanBeLocal")
public class DashboardEditForm extends FormLayout {
    private final TextField id = new TextField("ID");
    private final TextField name = new TextField("Name");
    private final Select<Visibility> visibility = new Select<>();
    private final DateTimePicker startDatetime = new DateTimePicker();
    private final DateTimePicker endDatetime = new DateTimePicker();
    private final TextArea description = new TextArea("Description");
    private final TextField broadcastUrl = new TextField("Broadcast URL");

    @Getter
    private final BeanValidationBinder<Dashboard> binder = new BeanValidationBinder<>(Dashboard.class);

    public DashboardEditForm(Dashboard dashboard) {
        id.setReadOnly(true);
        id.setValue(dashboard.getId());

        name.setRequired(true);
        name.setValue(dashboard.getName() != null ? dashboard.getName() : "");

        visibility.setLabel("Visibility");
        visibility.setItems(Visibility.values());
        visibility.setValue(dashboard.getVisibility() != null ? dashboard.getVisibility() : Visibility.PRIVATE);
        visibility.setRequiredIndicatorVisible(true);

        description.setWidthFull();
        description.setValue(dashboard.getDescription() != null ? dashboard.getDescription() : "");
        description.setMinRows(5);
        description.setMaxLength(3000);
        description.setHelperText(String.format("Markdown is supported, max %s characters", description.getMaxLength()));

        startDatetime.setLabel("Start Date and Time");
        startDatetime.setValue(dashboard.getStartDatetime() != null ? BrowserTimeZone.atLocalDateTime(dashboard.getStartDatetime()) : LocalDateTime.now());
        startDatetime.setWeekNumbersVisible(true);

        endDatetime.setLabel("End Date and Time");
        endDatetime.setValue(dashboard.getEndDatetime() != null ? BrowserTimeZone.atLocalDateTime(dashboard.getEndDatetime()) : LocalDateTime.now().plusHours(1));
        endDatetime.setWeekNumbersVisible(true);

        broadcastUrl.setValue(dashboard.getBroadcastUrl() != null ? dashboard.getBroadcastUrl() : "");

        setAutoResponsive(true);
        setExpandColumns(true);
        setExpandFields(true);

        add(id, name, visibility, startDatetime, endDatetime, description, broadcastUrl);

        binder.bindInstanceFields(this);
    }
}
