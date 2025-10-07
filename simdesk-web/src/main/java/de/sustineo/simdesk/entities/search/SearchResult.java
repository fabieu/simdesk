package de.sustineo.simdesk.entities.search;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchResult {
    private SearchType type;
    private String id;
    private String label;

    @Override
    public String toString() {
        return label;
    }
}
