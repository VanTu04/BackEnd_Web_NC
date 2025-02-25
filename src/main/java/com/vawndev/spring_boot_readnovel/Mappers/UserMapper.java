package com.vawndev.spring_boot_readnovel.Mappers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
