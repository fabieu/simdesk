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
public class StewardingEntrylistEntry {
    private Integer id;
    private Integer entrylistId;
    private Integer raceNumber;
    private Integer carModelId;
    private String teamName;
    private String displayName;
}
