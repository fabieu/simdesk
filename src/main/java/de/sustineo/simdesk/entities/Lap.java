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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Lap extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "session_id")
    private Integer sessionId;

    @OneToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "player_id")
    @CsvBindByName(column = "Driver")
    private Driver driver;

    @Column(name = "car_model_id")
    @CsvCustomBindByName(column = "Car Model", converter = CarModelConverter.class)
    private Integer carModelId;

    @Column(name = "lap_time_millis")
    @CsvCustomBindByName(column = "Lap time", converter = LapTimeConverter.class)
    private Long lapTimeMillis;

    @Column(name = "split1_millis")
    @CsvCustomBindByName(column = "Split 1 time", converter = LapTimeConverter.class)
    private Long split1Millis;

    @Column(name = "split2_millis")
    @CsvCustomBindByName(column = "Split 2 time", converter = LapTimeConverter.class)
    private Long split2Millis;

    @Column(name = "split3_millis")
    @CsvCustomBindByName(column = "Split 3 time", converter = LapTimeConverter.class)
    private Long split3Millis;

    @Column(name = "valid")
    @CsvBindByName(column = "Valid")
    private boolean valid;
}
