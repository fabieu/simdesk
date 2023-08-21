package de.sustineo.acc.servertools.services.converter.csv;

import com.opencsv.bean.AbstractBeanField;
import de.sustineo.acc.servertools.utils.FormatUtils;

public class LapTimeConverter<T, I> extends AbstractBeanField<T, I> {
    @Override
    protected Object convert(String value) {
        return Long.valueOf(value);
    }

    @Override
    public String convertToWrite(Object value) {
        return FormatUtils.formatLapTime((Long) value);
    }
}
