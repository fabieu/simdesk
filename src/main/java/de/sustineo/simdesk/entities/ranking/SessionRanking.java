package de.sustineo.simdesk.entities.ranking;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.services.converter.csv.CarModelConverter;
import de.sustineo.simdesk.services.converter.csv.LapTimeConverter;
import de.sustineo.simdesk.services.converter.csv.TotalTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRanking {
    private Session session;
    @CsvBindByName(column = "Position")
    private Integer ranking;
    @CsvBindByName(column = "Car Group")
    private Integer carId;
    private CarGroup carGroup;
    @CsvCustomBindByName(column = "Car Model", converter = CarModelConverter.class)
    private Integer carModelId;
    private Integer ballastKg;
    @CsvBindByName(column = "Car Number")
    private Integer raceNumber;
    @CsvBindAndSplitByName(column = "Drivers", elementType = Driver.class, writeDelimiter = ",")
    private List<Driver> drivers;
    @CsvCustomBindByName(column = "Fastest lap", converter = LapTimeConverter.class)
    private Long bestLapTimeMillis;
    private Long bestSplit1Millis;
    private Long bestSplit2Millis;
    private Long bestSplit3Millis;
    @CsvCustomBindByName(column = "Total time", converter = TotalTimeConverter.class)
    private Long totalTimeMillis;
    @CsvBindByName(column = "Laps")
    private Integer lapCount;

    public String getCarModelName() {
        return Car.getCarNameById(carModelId);
    }

    public String getTrackName() {
        return Track.getTrackNameById(session.getTrackId());
    }

    public boolean isValid() {
        return bestLapTimeMillis > 0 && lapCount > 0;
    }
}
