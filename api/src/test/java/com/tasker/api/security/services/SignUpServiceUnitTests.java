package com.tasker.api.security.services;

import com.tasker.api.security.dtos.SignUpInput;
import com.tasker.api.user.models.User;
import com.tasker.api.user.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SignUpServiceUnitTests implements SignUpServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SignUpService signUpService;

    @Test
    @Override
    public void shouldReturnUserCreatedWhenExecuteWithCorrectInput() {
        SignUpInput signInInput = new SignUpInput(
                "User 1",
                "user1@email.com",
                "User1@2024"
        );

        UUID userId = UUID.randomUUID();
        String encodedUserPassword = "$2a$12$feTc/GmuI1MeaUB7EldVpO72zvow8r2aEDl2CWh6ULypv2BnclrMy";

        User userToBeCreated = User.builder()
                .name(signInInput.name())
                .email(signInInput.email())
                .password(encodedUserPassword)
                .build();

        User userCreated = User.builder()
                .id(userId)
                .name(userToBeCreated.getName())
                .email(userToBeCreated.getEmail())
                .password(userToBeCreated.getPassword())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(this.passwordEncoder.encode(signInInput.password())).thenReturn(encodedUserPassword);
        when(this.userRepository.saveAndFlush(userToBeCreated)).thenReturn(userCreated);

        User user = this.signUpService.execute(signInInput);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @Override
    public void shouldThrowDataIntegrityExceptionWhenExecuteWithExistingEmail() {
        SignUpInput signInInput = new SignUpInput(
                "User 2",
                "user1@email.com",
                "User2@2024"
        );

        String encodedUserPassword = "$2a$12$XtonDcAfhXl..8a8jgzpEOz0OzLNG6Z9afKlAiolH30S3XdbBHa0m";

        User userToBeCreated = User.builder()
                .name(signInInput.name())
                .email(signInInput.email())
                .password(encodedUserPassword)
                .build();

        when(this.passwordEncoder.encode(signInInput.password())).thenReturn(encodedUserPassword);
        when(this.userRepository.saveAndFlush(userToBeCreated)).thenThrow(new DataIntegrityViolationException("Email already exists"));

        assertThrows(DataIntegrityViolationException.class, () -> this.signUpService.execute(signInInput));
    }
}
