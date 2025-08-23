package de.sustineo.simdesk.entities.json.kunos.acc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccEntrylist {
    @NotEmpty
    private List<@Valid AccEntrylistEntry> entries;
    @NotNull
    private Integer forceEntryList;

    public static AccEntrylist create() {
        List<AccEntrylistEntry> entries = new ArrayList<>();
        entries.add(AccEntrylistEntry.create());

        AccEntrylist entrylist = new AccEntrylist();
        entrylist.setEntries(entries);
        entrylist.setForceEntryList(1);

        return entrylist;
    }
}
