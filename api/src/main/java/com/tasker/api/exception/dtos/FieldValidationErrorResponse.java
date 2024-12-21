package com.tasker.api.exception.dtos;

import com.tasker.api.exception.pojos.FieldValidationError;

import java.util.List;

public record FieldValidationErrorResponse(
        String error,
        List<FieldValidationError> errors
) {
}
