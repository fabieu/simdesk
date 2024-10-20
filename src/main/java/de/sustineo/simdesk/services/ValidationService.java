package de.sustineo.simdesk.services;

import jakarta.validation.*;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {
    private final Validator validator;

    public ValidationService() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    public <T> void validate(T input) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
