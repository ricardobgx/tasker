package com.tasker.api.exception.handlers;

import com.tasker.api.exception.dtos.FieldValidationErrorResponse;
import com.tasker.api.exception.dtos.SimpleErrorResponse;
import com.tasker.api.exception.pojos.FieldValidationError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<FieldValidationError> errors = exception.getFieldErrors().stream()
                .map(fieldError -> new FieldValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        FieldValidationErrorResponse response = new FieldValidationErrorResponse("Invalid field content", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        SimpleErrorResponse response = new SimpleErrorResponse("Entity already exists");

        return ResponseEntity.badRequest().body(response);
    }
}
