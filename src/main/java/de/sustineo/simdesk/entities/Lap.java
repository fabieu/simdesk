package de.sustineo.simdesk.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import de.sustineo.simdesk.services.converter.csv.CarModelConverter;
import de.sustineo.simdesk.services.converter.csv.LapTimeConverter;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Lap extends Entity {
    private String id;
    private Integer sessionId;
    @CsvBindByName(column = "Driver")
    private Driver driver;
    @Deprecated
    private CarGroup carGroup;
    @CsvCustomBindByName(column = "Car Model", converter = CarModelConverter.class)
    private Integer carModelId;
    @CsvCustomBindByName(column = "Lap time", converter = LapTimeConverter.class)
    private Long lapTimeMillis;
    @CsvCustomBindByName(column = "Split 1 time", converter = LapTimeConverter.class)
    private Long split1Millis;
    @CsvCustomBindByName(column = "Split 2 time", converter = LapTimeConverter.class)
    private Long split2Millis;
    @CsvCustomBindByName(column = "Split 3 time", converter = LapTimeConverter.class)
    private Long split3Millis;
    @CsvBindByName(column = "Valid")
    private boolean valid;
}
