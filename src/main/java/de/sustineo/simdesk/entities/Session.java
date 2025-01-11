package de.sustineo.simdesk.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class Session extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_type")
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Column(name = "race_weekend_index")
    private Integer raceWeekendIndex;

    @Column(name = "server_name")
    private String serverName;

    @Column(name = "track_id")
    private String trackId;

    @Column(name = "wet_session")
    private Boolean wetSession;

    @Column(name = "car_count")
    private Integer carCount;

    @Column(name = "session_datetime")
    private Instant sessionDatetime;

    @Column(name = "file_checksum")
    private String fileChecksum;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_content")
    private String fileContent;

    @Column(name = "insert_datetime")
    @CreationTimestamp
    private Instant insertDatetime;
}
