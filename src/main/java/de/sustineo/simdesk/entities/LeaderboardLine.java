package de.sustineo.simdesk.entities;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import de.sustineo.simdesk.entities.json.kunos.acc.AccCupCategory;
import de.sustineo.simdesk.services.converter.csv.CarModelConverter;
import de.sustineo.simdesk.services.converter.csv.LapTimeConverter;
import de.sustineo.simdesk.services.converter.csv.TotalTimeConverter;
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
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private Session session;

    @CsvBindByName(column = "Position")
    @Column(name = "ranking")
    private Integer ranking;

    @Enumerated(EnumType.STRING)
    @Column(name = "cup_category")
    private AccCupCategory cupCategory;

    @Column(name = "car_id")
    private Integer carId;

    @CsvCustomBindByName(column = "Car Model", converter = CarModelConverter.class)
    @Column(name = "car_model_id")
    private Integer carModelId;

    @Column(name = "ballast_kg")
    private Integer ballastKg;

    @CsvBindByName(column = "Car Number")
    @Column(name = "race_number")
    private Integer raceNumber;

    @CsvBindAndSplitByName(column = "Drivers", elementType = Driver.class, writeDelimiter = ",")
    @OneToMany
    @JoinColumns({
            @JoinColumn(name = "session_id", referencedColumnName = "session_id"),
            @JoinColumn(name = "car_id", referencedColumnName = "car_id")
    })
    private List<LeaderboardDriver> drivers;

    @CsvCustomBindByName(column = "Fastest lap", converter = LapTimeConverter.class)
    @Column(name = "best_lap_time_millis")
    private Long bestLapTimeMillis;

    @Column(name = "best_split1_millis")
    private Long bestSplit1Millis;

    @Column(name = "best_split2_millis")
    private Long bestSplit2Millis;

    @Column(name = "best_split3_millis")
    private Long bestSplit3Millis;

    @CsvCustomBindByName(column = "Total time", converter = TotalTimeConverter.class)
    @Column(name = "total_time_millis")
    private Long totalTimeMillis;

    @CsvBindByName(column = "Laps")
    @Column(name = "lap_count")
    private Integer lapCount;

    @CreationTimestamp
    @Column(name = "insert_datetime")
    private Instant insertDatetime;
}
