package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserDetailReponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;

import java.util.List;

public interface UserService {
    PageResponse<UserDetailReponse> getAllUser(PageRequest req);
    UserResponse createUser(UserCreationRequest userRequest);
//    UserResponse getMyInfor();

    UserResponse updateUser(String userId, UserUpdateRequest request);
    void deleteUser(String userId);
    List<UserResponse> getAllUsers();
    UserResponse getUser(String userId);
    User getUserByEmail(String email);
}
