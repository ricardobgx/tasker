package com.tasker.api.security.services;

public interface SignUpServiceTests {
    void shouldReturnUserCreatedWhenExecuteWithCorrectInput();

    void shouldThrowDataIntegrityExceptionWhenExecuteWithExistingEmail();
}
