package de.sustineo.simdesk.entities.stewarding;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StewardingEntrylistDriver {
    private String id;
    private String entryId;
    private String firstName;
    private String lastName;
    private String shortName;
    private String steamId;
    private Integer category;
}
