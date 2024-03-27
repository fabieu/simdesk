package de.sustineo.simdesk.entities.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationData {
    private List<ValidationRule> rules;
    private List<ValidationError> errors = new ArrayList<>();
}
