package com.tasker.api.security.services;

import com.tasker.api.security.dtos.SignUpInput;
import com.tasker.api.user.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class SignUpServiceIntegrationTests implements SignUpServiceTests {
    @Autowired
    private SignUpService signUpService;

    @Test
    @Override
    @Sql(statements = "DELETE FROM users;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnUserCreatedWhenExecuteWithCorrectInput() {
        SignUpInput signInInput = new SignUpInput(
                "User 1",
                "user1@email.com",
                "User1@2024"
        );

        User user = this.signUpService.execute(signInInput);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @Override
    @Sql(statements = "DELETE FROM users;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldThrowDataIntegrityExceptionWhenExecuteWithExistingEmail() {
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

        assertThrows(DataIntegrityViolationException.class, () -> this.signUpService.execute(signInInput));
    }
}
