package com.tasker.api.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpInput(
        @NotBlank
        String name,
        @NotBlank @Email
        String email,
        @NotBlank @Size(min = 8)
        String password
) {
}
