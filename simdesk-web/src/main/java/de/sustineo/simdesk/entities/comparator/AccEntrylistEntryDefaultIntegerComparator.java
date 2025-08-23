package de.sustineo.simdesk.entities.comparator;

import java.util.Comparator;

/**
 * Handles the comparison of two integers, where -1 is considered the lowest value.
 * -1 represents a default value in the context of an AccEntrylistEntry.
 */
public class AccEntrylistEntryDefaultIntegerComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        if (o1 == -1 && o2 == -1) {
            return 0;
        } else if (o1 == -1) {
            return 1;
        } else if (o2 == -1) {
            return -1;
        } else {
            return o1.compareTo(o2);
        }
    }
}
