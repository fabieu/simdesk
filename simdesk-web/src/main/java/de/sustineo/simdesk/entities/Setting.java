package de.sustineo.simdesk.entities;

import lombok.Data;

import java.time.Instant;

@Data
public class Setting {
    public static final String BOP_PROVIDERS = "bop.providers";

    private Integer id;
    private String key;
    private String value;
    private boolean active;
    private Instant updateDatetime;
}
