package de.sustineo.simdesk.entities.entrylist;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Entrylist {
    public Entrylist() {
        this.entries = new ArrayList<>();
        this.forceEntryList = 1;
    }

    @NotEmpty
    private List<@Valid Entry> entries;
    @NotNull
    private Integer forceEntryList;
}
