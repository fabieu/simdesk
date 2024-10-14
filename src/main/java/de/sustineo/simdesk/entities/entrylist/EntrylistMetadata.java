package de.sustineo.simdesk.entities.entrylist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrylistMetadata {
    private String fileName;
    private String type;
    private Long contentLength;
}
