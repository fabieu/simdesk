package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AccBop {
    @NotNull
    private int configVersion = 0;
    @NotNull
    private List<@Valid AccBopEntry> entries = new ArrayList<>();

    public AccBop(List<AccBopEntry> entries) {
        this.entries = entries;
    }

    @JsonIgnore
    public boolean isMultiTrack() {
        return entries.stream()
                .map(AccBopEntry::getTrackId)
                .distinct()
                .count() > 1;
    }

    @JsonIgnore
    public List<AccCar> getCars() {
        return entries.stream()
                .map(entry -> AccCar.getCarById(entry.getCarId()))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Track getTrack() {
        return entries.stream()
                .map(entry -> Track.getByAccId(entry.getTrackId()))
                .findFirst()
                .orElse(null);
    }

}
