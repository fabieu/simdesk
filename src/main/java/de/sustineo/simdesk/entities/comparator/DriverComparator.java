package de.sustineo.simdesk.entities.comparator;

import de.sustineo.simdesk.entities.Driver;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class DriverComparator implements Comparator<Driver> {
    @Override
    public int compare(Driver d1, Driver d2) {
        return new CompareToBuilder()
                .append(d1.getLastName(), d2.getLastName())
                .append(d1.getFirstName(), d2.getFirstName())
                .build();
    }
}
