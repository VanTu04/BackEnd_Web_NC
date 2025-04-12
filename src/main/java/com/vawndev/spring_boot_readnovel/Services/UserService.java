package com.vawndev.spring_boot_readnovel.Services;

import java.util.List;

import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;

public interface UserService {
    UserResponse createUser(UserCreationRequest userRequest);
//    UserResponse getMyInfor();
    UserResponse updateUser(String userId, UserUpdateRequest request);
    void deleteUser(String userId);
    List<UserResponse> getAllUsers();
    UserResponse getUser(String userId);
    User getUserByEmail(String email);
    void resetPassword(String email, String newPassword);
}
