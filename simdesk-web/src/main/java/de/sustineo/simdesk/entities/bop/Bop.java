package de.sustineo.simdesk.entities.bop;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Bop {
    private String trackId;
    private Integer carId;
    private Integer restrictor;
    private Integer ballastKg;
    private String username;
    private Boolean active;
    private Instant updateDatetime;

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
