package de.sustineo.simdesk.entities.output;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Getter
public class ServiceResponse<T> {
    private final HttpStatus status;
    private final Integer statusCode;
    private T data;
    private Throwable exception;

    public ServiceResponse(HttpStatus status) {
        this.status = status;
        this.statusCode = status.value();
    }

    public ServiceResponse(HttpStatus status, T data) {
        this(status);
        this.data = data;
    }

    public ResponseEntity<ServiceResponse<T>> toResponseEntity() {
        return new ResponseEntity<>(this, this.status);
    }
}