package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GridFilter {
    public static Component createTextFieldHeader(Consumer<String> filterChangeConsumer) {
        VerticalLayout layout = createHeaderLayout();

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setPrefixComponent(VaadinIcon.SEARCH.create());
        textField.setPlaceholder("Search");
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));

        layout.add(textField);
        return layout;
    }

    public static <T extends Enum<T>> Component createSelectHeader(Consumer<T> filterChangeConsumer, Supplier<? extends Collection<T>> itemsSupplier) {
        VerticalLayout layout = createHeaderLayout();

        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setWidthFull();
        comboBox.setItems(itemsSupplier.get());
        comboBox.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        comboBox.setPrefixComponent(VaadinIcon.SEARCH.create());
        comboBox.setPlaceholder("Search");
        comboBox.setClearButtonVisible(true);
        comboBox.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));

        layout.add(comboBox);
        return layout;
    }

    private static VerticalLayout createHeaderLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");
        return layout;
    }

    protected boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || (value != null && value.toLowerCase().contains(searchTerm.toLowerCase()));
    }

    protected <T> boolean matches(T value, T searchTerm) {
        return searchTerm == null || (value != null && value.equals(searchTerm));
    }
}
