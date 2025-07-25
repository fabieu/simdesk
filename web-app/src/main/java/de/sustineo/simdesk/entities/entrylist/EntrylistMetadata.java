package de.sustineo.simdesk.entities.entrylist;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EntrylistMetadata {
    private String fileName;
    private String type;
    private Long contentLength;
}
