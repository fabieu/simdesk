package de.sustineo.simdesk.views.filter.combobox;

import com.vaadin.flow.component.combobox.ComboBox;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CarFilter implements ComboBox.ItemFilter<AccCar> {
    final String validCarGroups = String.join(
            ", ",
            CarGroup.getValid().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet())
    );

    @Override
    public boolean test(AccCar car, String filter) {
        if (car == null || filter == null) {
            return false;
        }

        String normalizedFilter = filter.toLowerCase();
        return Stream.of(car.getModel(), car.getGroup().name())
                .map(String::toLowerCase)
                .anyMatch(s -> s.contains(normalizedFilter));
    }

    public String getHelperText() {
        return String.format("Available filters: Car Model, Car Group (%s)", validCarGroups);
    }
}
