package de.sustineo.simdesk.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leaderboard_driver")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaderboardDriver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id")
    private Driver driver;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "drive_time_millis")
    private Long driveTimeMillis;
}
