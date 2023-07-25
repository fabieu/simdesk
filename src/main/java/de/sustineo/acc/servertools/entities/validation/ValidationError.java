package de.sustineo.acc.servertools.entities.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ValidationError {
    private ValidationRule rule;
    private String message;
    private List<Object> references;

    public ValidationError(ValidationRule validationRule, String message, Object reference) {
        this(validationRule, message, List.of(reference));
    }
}
