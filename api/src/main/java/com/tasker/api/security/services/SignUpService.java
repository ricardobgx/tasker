package com.tasker.api.security.services;

import com.tasker.api.security.dtos.SignUpInput;
import com.tasker.api.user.mappers.UserMapper;
import com.tasker.api.user.models.User;
import com.tasker.api.user.repositories.UserRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User execute(SignUpInput signUpInput) {
        User user = this.userMapper.convertSignUpInputToUser(signUpInput);

        String encodedPassword = this.passwordEncoder.encode(signUpInput.password());

        user.setPassword(encodedPassword);

        return this.userRepository.saveAndFlush(user);
    }
}
