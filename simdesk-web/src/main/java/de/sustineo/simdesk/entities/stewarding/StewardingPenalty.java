package de.sustineo.simdesk.entities.stewarding;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StewardingPenalty {
    private Integer id;
    private String code;
    private String title;
    private String description;
}
