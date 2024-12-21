package com.tasker.api.security.controllers;

import com.tasker.api.security.dtos.SignUpInput;
import com.tasker.api.security.services.SignUpService;
import com.tasker.api.user.dtos.UserDetails;
import com.tasker.api.user.models.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private SignUpService signUpService;

    @PostMapping("sign-up")
    public ResponseEntity<UserDetails> signUp(
            @Valid @RequestBody SignUpInput signUpInput,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        User user = this.signUpService.execute(signUpInput);

        URI userUri = uriComponentsBuilder.path("/users/{userId}").buildAndExpand(user.getId()).toUri();

        UserDetails userDetails = new UserDetails(user);

        return ResponseEntity.created(userUri).body(userDetails);
    }
}
