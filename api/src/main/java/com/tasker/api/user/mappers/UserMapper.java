package com.tasker.api.user.mappers;

import com.tasker.api.security.dtos.SignUpInput;
import com.tasker.api.user.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    User convertSignUpInputToUser(SignUpInput signUpInput);
}
