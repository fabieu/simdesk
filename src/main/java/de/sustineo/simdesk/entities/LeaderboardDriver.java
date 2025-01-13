package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.utils.FormatUtils;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Driver driver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private Session session;

    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "drive_time_millis")
    private Long driveTimeMillis;

    @Transient
    private Integer validLapsCount;

    @Transient
    private Integer invalidLapsCount;

    @SuppressWarnings("unused")
    public String getPrettyDriveTime() {
        return FormatUtils.formatDriveTime(driveTimeMillis);
    }
}
