package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.function.Consumer;

public class FilterUtils {
    public static Component createFilterHeader(Consumer<String> filterChangeConsumer) {
        VerticalLayout layout = new VerticalLayout();

        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        textField.setWidthFull();
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setClearButtonVisible(true);
        textField.setPlaceholder("Search");
        textField.setPrefixComponent(VaadinIcon.SEARCH.create());
        layout.add(textField);

        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }
}
