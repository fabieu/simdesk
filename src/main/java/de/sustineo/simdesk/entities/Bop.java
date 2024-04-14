package de.sustineo.simdesk.entities;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
public class Bop {
    private String trackId;
    private Integer carId;
    private Integer restrictor;
    private Integer ballastKg;
    private String username;
    private boolean active;
    private Instant updateDatetime;

    public Bop() {
        this.restrictor = 0;
        this.ballastKg = 0;
        this.active = true;
        this.updateDatetime = Instant.now();
    }

    public Bop(String trackId, Integer carId, boolean active) {
        this();
        this.trackId = trackId;
        this.carId = carId;
        this.active = active;
    }

    @Builder
    public Bop(String trackId, Integer carId, Integer restrictor, Integer ballastKg, String username, boolean active, Instant updateDatetime) {
        this();
        this.trackId = trackId;
        this.carId = carId;
        this.restrictor = restrictor;
        this.ballastKg = ballastKg;
        this.username = username;
        this.active = active;
        this.updateDatetime = updateDatetime;
    }

    /**
     * The Grid editor needs to know what has changed in order to close the right thing.
     * Make sure that equals and hashCode of <code>Bop</code> uses unique attributes.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bop bop)) return false;

        return trackId.equals(bop.trackId) && carId.equals(bop.carId);
    }

    @Override
    public int hashCode() {
        int result = trackId.hashCode();
        result = 31 * result + carId.hashCode();
        return result;
    }
}
