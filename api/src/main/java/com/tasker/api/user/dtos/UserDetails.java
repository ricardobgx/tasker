package com.tasker.api.user.dtos;

import com.tasker.api.user.models.User;

import java.util.UUID;

public record UserDetails(
        UUID id,
        String name,
        String email
) {
    public UserDetails(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
