package de.sustineo.simdesk.views.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.dom.Style;
import de.sustineo.simdesk.entities.json.kunos.acc.AccBopEntry;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

@Getter
public class BopEditField extends CustomField<AccBopEntry> {
    private final TextField ballastField = new TextField();
    private final TextField restrictorField = new TextField();
    private final Binder<AccBopEntry> binder = new Binder<>(AccBopEntry.class);

    public BopEditField(AccBopEntry entry) {
        Integer carId = entry.getCarId();
        String carModel = AccCar.getModelById(carId);
        setLabel(String.format("%s (ID: %s)", carModel, carId));

        ballastField.setSuffixComponent(new Span("kg"));
        binder.forField(ballastField)
                .asRequired()
                .withConverter(new StringToIntegerConverter("Must be a number"))
                .withValidator(value -> value >= -40 && value <= 40, "Value must be between -40 and 40kg")
                .bind(AccBopEntry::getBallastKg, AccBopEntry::setBallastKg);

        restrictorField.setSuffixComponent(new Span("%"));
        binder.forField(restrictorField)
                .asRequired()
                .withConverter(new StringToIntegerConverter("Must be a number"))
                .withValidator(value -> value >= 0 && value <= 20, "Value must be between 0 and 20%")
                .bind(AccBopEntry::getRestrictor, AccBopEntry::setRestrictor);

        FlexLayout layout = new FlexLayout();
        layout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-m)");
        layout.add(ballastField, restrictorField);

        add(layout);
        binder.setBean(entry);
    }

    @Override
    protected AccBopEntry generateModelValue() {
        return AccBopEntry.builder()
                .ballastKg(NumberUtils.toInt(ballastField.getValue(), 0))
                .restrictor(NumberUtils.toInt(restrictorField.getValue(), 0))
                .build();
    }

    @Override
    protected void setPresentationValue(AccBopEntry bopEntry) {
        ballastField.setValue(String.valueOf(bopEntry.getBallastKg()));
        restrictorField.setValue(String.valueOf(bopEntry.getRestrictor()));
    }
}
