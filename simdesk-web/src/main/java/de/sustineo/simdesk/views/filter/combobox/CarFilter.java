package de.sustineo.simdesk.views.filter.combobox;

import com.vaadin.flow.component.combobox.ComboBox;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CarFilter implements ComboBox.ItemFilter<AccCar> {
    private static final CarFilter INSTANCE = new CarFilter();

    public static CarFilter getInstance() {
        return INSTANCE;
    }

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
        String validCarGroups = String.join(", ",
                CarGroup.getValid().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );

        return String.format("Available filters: Car Model, Car Group (%s)", validCarGroups);
    }
}
