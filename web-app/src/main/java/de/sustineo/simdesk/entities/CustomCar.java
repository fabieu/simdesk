package de.sustineo.simdesk.entities;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomCar {
    @NotNull
    private Integer carNumber;
    @NotNull
    private String customCar;
    private Boolean overrideCarModelForCustomCar;
}
