package com.tasker.api.security.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasker.api.security.dtos.SignUpInput;
import com.tasker.api.security.services.SignUpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AuthenticationControllerIntegrationTests implements AuthenticationControllerTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SignUpService signUpService;

    @Test
    @Override
    @Sql(statements = "DELETE FROM users;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnStatus200WhenSignUpWithCorrectInput() {
        try {
            SignUpInput signInInput = new SignUpInput(
                    "User 1",
                    "user1@email.com",
                    "User1@2024"
            );

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
    @Sql(statements = "DELETE FROM users;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @Sql(statements = "DELETE FROM users;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnStatus404AndErrorMessageWhenSignUpWithExistingEmail() {
        try {
            SignUpInput existingUserSignUpInput = new SignUpInput(
                    "User 1",
                    "user1@email.com",
                    "User1@2024"
            );

            this.signUpService.execute(existingUserSignUpInput);

            SignUpInput signInInput = new SignUpInput(
                    "User 2",
                    "user1@email.com",
                    "User2@2024"
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
                    .andExpect(jsonPath("$.error", is("Entity already exists")));
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
