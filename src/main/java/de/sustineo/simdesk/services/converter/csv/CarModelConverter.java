package de.sustineo.simdesk.services.converter.csv;

import com.opencsv.bean.AbstractBeanField;
import de.sustineo.simdesk.entities.Car;

public class CarModelConverter<T, I> extends AbstractBeanField<T, I> {
    @Override
    protected Object convert(String value) {
        return Car.getModelIdByName(value);
    }

    @Override
    public String convertToWrite(Object value) {
        return Car.getNameById((Integer) value);
    }
}
