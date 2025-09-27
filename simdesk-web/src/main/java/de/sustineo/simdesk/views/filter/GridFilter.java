package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
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
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        textField.setWidthFull();
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setClearButtonVisible(true);
        textField.setPlaceholder("Search");
        textField.setPrefixComponent(VaadinIcon.SEARCH.create());
        layout.add(textField);

        return layout;
    }

    public static <T extends Enum<T> & GridEnum> Component createSelectHeader(Consumer<T> filterChangeConsumer, Supplier<? extends Collection<T>> itemsSupplier) {
        VerticalLayout layout = createHeaderLayout();

        Select<T> select = new Select<>();
        select.setWidthFull();
        select.setItems(itemsSupplier.get());
        select.setItemLabelGenerator(item -> item == null ? "" : item.getLabel());
        select.setEmptySelectionAllowed(true);
        select.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        select.addThemeVariants(SelectVariant.LUMO_SMALL);
        select.setPlaceholder("Select");
        select.setPrefixComponent(VaadinIcon.SEARCH.create());

        layout.add(select);

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
