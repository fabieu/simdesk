package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.entities.json.kunos.acc.AccCupCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "leaderboard_line")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaderboardLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "cup_category")
    @Enumerated(EnumType.STRING)
    private AccCupCategory cupCategory;

    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "car_model_id")
    private Integer carModelId;

    @Column(name = "ballast_kg")
    private Integer ballastKg;

    @Column(name = "race_number")
    private Integer raceNumber;

    @OneToMany
    @JoinColumn(name = "player_id")
    private List<Driver> drivers;

    @Column(name = "best_lap_time_millis")
    private Long bestLapTimeMillis;

    @Column(name = "best_split1_millis")
    private Long bestSplit1Millis;

    @Column(name = "best_split2_millis")
    private Long bestSplit2Millis;

    @Column(name = "best_split3_millis")
    private Long bestSplit3Millis;

    @Column(name = "total_time_millis")
    private Long totalTimeMillis;

    @Column(name = "lap_count")
    private Integer lapCount;

    @Column(name = "insert_datetime")
    @CreationTimestamp
    private Instant insertDatetime;
}
