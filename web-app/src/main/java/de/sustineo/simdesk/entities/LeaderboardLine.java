package de.sustineo.simdesk.entities;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import de.sustineo.simdesk.entities.json.kunos.acc.AccCupCategory;
import de.sustineo.simdesk.services.converter.csv.CarModelConverter;
import de.sustineo.simdesk.services.converter.csv.LapTimeConverter;
import de.sustineo.simdesk.services.converter.csv.TotalTimeConverter;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LeaderboardLine {
    private Integer id;
    private Session session;
    @CsvBindByName(column = "Position")
    private Integer ranking;
    private AccCupCategory cupCategory;
    @CsvBindByName(column = "Car Group")
    private Integer carId;
    @CsvCustomBindByName(column = "Car Model", converter = CarModelConverter.class)
    private Integer carModelId;
    private Integer ballastKg;
    @CsvBindByName(column = "Car Number")
    private Integer raceNumber;
    @CsvBindAndSplitByName(column = "Drivers", elementType = Driver.class, writeDelimiter = ";")
    private List<Driver> drivers;
    @CsvCustomBindByName(column = "Fastest lap", converter = LapTimeConverter.class)
    private Long bestLapTimeMillis;
    private Long bestSector1Millis;
    private Long bestSector2Millis;
    private Long bestSector3Millis;
    @CsvCustomBindByName(column = "Total time", converter = TotalTimeConverter.class)
    private Long totalTimeMillis;
    @CsvBindByName(column = "Laps")
    private Integer lapCount;

    public static LeaderboardLine create() {
        return new LeaderboardLine();
    }
}
