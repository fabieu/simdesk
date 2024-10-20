package de.sustineo.simdesk.entities.json.kunos.acc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccEntrylist {
    public AccEntrylist() {
        this.entries = new ArrayList<>();
        this.forceEntryList = 0;
    }

    @NotEmpty
    private List<@Valid AccEntrylistEntry> entries;
    @NotNull
    private Integer forceEntryList;
}
