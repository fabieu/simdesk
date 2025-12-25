package de.sustineo.simdesk.entities.stewarding;

import lombok.Data;

import java.util.Set;

public class StewardingEntrylist {
    private Set<Entry> entries;

    @Data
    public static class Entry {
        private Integer carNumber;
        private String teamName;
        private Set<Driver> drivers;
    }

    @Data
    public static class Driver {
        private String name;
    }
}
