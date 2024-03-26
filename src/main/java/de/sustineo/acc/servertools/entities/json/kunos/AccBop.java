package de.sustineo.acc.servertools.entities.json.kunos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.sustineo.acc.servertools.entities.Car;
import de.sustineo.acc.servertools.entities.Track;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AccBop {
    @NotNull
    private int configVersion = 0;
    @NotNull
    private List<@Valid AccBopEntry> entries = new ArrayList<>();

    @JsonIgnore
    public boolean isMultiTrack() {
        return entries.stream()
                .map(AccBopEntry::getTrackId)
                .distinct()
                .count() > 1;
    }

    @JsonIgnore
    public List<Car> getCars() {
        return entries.stream()
                .map(entry -> new Car(entry.getCarId(), entry.getCarName()))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Track getTrack() {
        return entries.stream()
                .map(entry -> new Track(entry.getTrackId(), entry.getTrackName()))
                .findFirst()
                .orElse(null);
    }

}
