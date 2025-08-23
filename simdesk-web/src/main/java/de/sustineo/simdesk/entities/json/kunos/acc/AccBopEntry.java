package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccBopEntry {
    @NotEmpty
    @JsonProperty("track")
    private String trackId;
    @NotNull
    @JsonProperty("carModel")
    private Integer carId;
    @Min(-40)
    @Max(40)
    private Integer ballastKg;
    @Min(-20)
    @Max(20)
    private Integer restrictor;
}
