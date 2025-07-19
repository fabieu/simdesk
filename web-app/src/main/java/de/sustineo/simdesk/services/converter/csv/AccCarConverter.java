package de.sustineo.simdesk.services.converter.csv;

import com.opencsv.bean.AbstractBeanField;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

public class AccCarConverter<T, I> extends AbstractBeanField<T, I> {
    @Override
    protected Object convert(String value) {
        return AccCar.getIdByName(value);
    }

    @Override
    public String convertToWrite(Object value) {
        return AccCar.getModelById((Integer) value);
    }
}
