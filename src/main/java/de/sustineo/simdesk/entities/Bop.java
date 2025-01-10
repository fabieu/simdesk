package de.sustineo.simdesk.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "bop")
@IdClass(Bop.BopId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Bop {
    @Id
    @Column(name = "track_id")
    private String trackId;
    @Id
    @Column(name = "car_id")
    private Integer carId;
    @Column(name = "restrictor")
    private Integer restrictor;
    @Column(name = "ballast_kg")
    private Integer ballastKg;
    @Column(name = "username")
    private String username;
    @Column(name = "active")
    private Boolean active;
    @UpdateTimestamp
    @Column(name = "update_datetime")
    private Instant updateDatetime;

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BopId implements Serializable {
        private String trackId;
        private Integer carId;
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
