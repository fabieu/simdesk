package de.sustineo.simdesk.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import de.sustineo.simdesk.services.converter.csv.CarModelConverter;
import de.sustineo.simdesk.services.converter.csv.LapTimeConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lap")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class Lap extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private Session session;

    @CsvBindByName(column = "Driver")
    @OneToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "player_id")
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_group")
    private CarGroup carGroup;

    @CsvCustomBindByName(column = "Car Model", converter = CarModelConverter.class)
    @Column(name = "car_model_id")
    private Integer carModelId;

    @CsvCustomBindByName(column = "Lap time", converter = LapTimeConverter.class)
    @Column(name = "lap_time_millis")
    private Long lapTimeMillis;

    @CsvCustomBindByName(column = "Split 1 time", converter = LapTimeConverter.class)
    @Column(name = "split1_millis")
    private Long split1Millis;

    @CsvCustomBindByName(column = "Split 2 time", converter = LapTimeConverter.class)
    @Column(name = "split2_millis")
    private Long split2Millis;

    @CsvCustomBindByName(column = "Split 3 time", converter = LapTimeConverter.class)
    @Column(name = "split3_millis")
    private Long split3Millis;

    @CsvBindByName(column = "Valid")
    @Column(name = "valid")
    private boolean valid;
}
