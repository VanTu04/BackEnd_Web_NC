package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest userRequest);
//    UserResponse getMyInfor();
    UserResponse updateUser(String userId, UserUpdateRequest request);
    void deleteUser(String userId);
    List<UserResponse> getAllUsers();
    UserResponse getUser(String userId);
    User getUserByEmail(String email);
}
