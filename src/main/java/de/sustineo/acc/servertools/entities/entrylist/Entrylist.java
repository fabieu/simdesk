package de.sustineo.acc.servertools.entities.entrylist;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class Entrylist {
    @NotEmpty
    private List<@Valid Entry> entries;
    @NotNull
    private Integer forceEntryList;
}
