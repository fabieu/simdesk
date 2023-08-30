package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.utils.FormatUtils;

import java.util.List;

@Route(value = "/converter", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "Converter")
@AnonymousAllowed
public class ConverterView extends VerticalLayout {
    public ConverterView(ComponentUtils componentUtils) {

        setSizeFull();
        setPadding(false);

        addAndExpand(createConverterForms());
        add(componentUtils.createFooter());
    }

    private void addConverterFormLayout(VerticalLayout layout, List<Component> components) {
        if (components.size() != 3) {
            throw new IllegalArgumentException("Converter form layout must have exactly 3 components");
        }

        FormLayout formLayout = new FormLayout();
        formLayout.add(components);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("768px", 3)
        );

        layout.add(formLayout);
    }

    private Component createConverterForms() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        addConverterFormLayout(layout, createLapTimeConverterComponents());

        return layout;
    }

    private List<Component> createLapTimeConverterComponents() {
        TextField timeFormattedField = new TextField("Time (formatted)");
        timeFormattedField.setReadOnly(true);

        // Update time formatted field when time field changes
        NumberField timeField = new NumberField("Time (milliseconds)");
        timeField.setMin(0L);
        timeField.addValueChangeListener(event -> {
            if (timeField.getValue() == null || timeField.getValue() <= 0L) {
                return;
            }
            String value = FormatUtils.formatTotalTime(timeField.getValue().longValue());
            timeFormattedField.setValue(value);
        });

        // Update time formatted field when button is clicked
        Button timeConversionButton = new Button("Convert");
        timeConversionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        timeConversionButton.addClickListener(event -> {
            if (timeField.getValue() == null || timeField.getValue() <= 0L) {
                return;
            }
            String value = FormatUtils.formatTotalTime(timeField.getValue().longValue());
            timeFormattedField.setValue(value);
        });

        return List.of(timeField, timeConversionButton, timeFormattedField);
    }
}
