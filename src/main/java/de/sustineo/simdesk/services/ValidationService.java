package de.sustineo.simdesk.services;

import jakarta.validation.*;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    public <T> void validate(T input) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
