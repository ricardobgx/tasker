package com.tasker.api.security.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasker.api.security.configs.SecurityConfig;
import com.tasker.api.security.dtos.SignUpInput;
import com.tasker.api.security.services.SignUpService;
import com.tasker.api.user.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
public class AuthenticationControllerUnitTests implements AuthenticationControllerTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignUpService signUpService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @Override
    public void shouldReturnStatus200WhenSignUpWithCorrectInput() {
        try {
            SignUpInput signInInput = new SignUpInput(
                    "User 1",
                    "user1@email.com",
                    "User1@2024"
            );

            UUID userId = UUID.randomUUID();
            String encodedUserPassword = "$2a$12$feTc/GmuI1MeaUB7EldVpO72zvow8r2aEDl2CWh6ULypv2BnclrMy";

            User userCreated = User.builder()
                    .id(userId)
                    .name(signInInput.name())
                    .email(signInInput.email())
                    .password(encodedUserPassword)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(this.signUpService.execute(signInInput)).thenReturn(userCreated);

            this.mockMvc
                    .perform(post("/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(signInInput)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(header().exists("location"))
                    .andExpect(jsonPath("$").isNotEmpty())
                    .andExpect(jsonPath("$.id").isNotEmpty());
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Test
    @Override
    public void shouldReturnStatus400AndErrorMessagesWhenSignUpWithInvalidEmail() {
        try {
            SignUpInput signInInput = new SignUpInput(
                    "User 1",
                    "user1",
                    "User1@2024"
            );

            this.mockMvc
                    .perform(post("/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(signInInput)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty())
                    .andExpect(jsonPath("$.error").isNotEmpty())
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors[0].field").isNotEmpty())
                    .andExpect(jsonPath("$.errors[0].field", is("email")));
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Test
    @Override
    public void shouldReturnStatus404AndErrorMessageWhenSignUpWithExistingEmail() {
        try {
            SignUpInput signInInput = new SignUpInput(
                    "User 2",
                    "user1@email.com",
                    "User2@2024"
            );

            when(this.signUpService.execute(signInInput)).thenThrow(new DataIntegrityViolationException("Email already exists"));

            this.mockMvc
                    .perform(post("/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(signInInput)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty())
                    .andExpect(jsonPath("$.error").isNotEmpty())
                    .andExpect(jsonPath("$.error", is("Entity already exists")));
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
