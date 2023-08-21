package de.sustineo.acc.servertools.services.converter.csv;

import com.opencsv.bean.AbstractBeanField;
import de.sustineo.acc.servertools.entities.Car;

public class CarModelConverter<T, I> extends AbstractBeanField<T, I> {
    @Override
    protected Object convert(String value) {
        return Car.getCarIdByName(value);
    }

    @Override
    public String convertToWrite(Object value) {
        return Car.getCarNameById((Integer) value);
    }
}
