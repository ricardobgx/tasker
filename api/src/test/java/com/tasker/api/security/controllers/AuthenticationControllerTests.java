package com.tasker.api.security.controllers;

public interface AuthenticationControllerTests {
    void shouldReturnStatus200WhenSignUpWithCorrectInput();

    void shouldReturnStatus400AndErrorMessagesWhenSignUpWithInvalidEmail();

    void shouldReturnStatus404AndErrorMessageWhenSignUpWithExistingEmail();
}
