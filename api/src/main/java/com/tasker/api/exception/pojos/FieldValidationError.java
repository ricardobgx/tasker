package com.tasker.api.exception.pojos;

public record FieldValidationError(
        String field,
        String error
) {
}
