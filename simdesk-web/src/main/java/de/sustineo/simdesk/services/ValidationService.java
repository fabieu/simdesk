package de.sustineo.simdesk.services;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {
    private final Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T input) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
