package de.sustineo.simdesk.services.converter.csv;

import com.opencsv.bean.AbstractBeanField;
import de.sustineo.simdesk.utils.FormatUtils;

public class TotalTimeConverter<T, I> extends AbstractBeanField<T, I> {
    @Override
    protected Object convert(String value) {
        return Long.valueOf(value);
    }

    @Override
    public String convertToWrite(Object value) {
        return FormatUtils.formatTotalTime((Long) value);
    }
}
