package com.vawndev.spring_boot_readnovel.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.ConfirmOtpRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserDetailReponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    PageResponse<UserDetailReponse> getAllUser(PageRequest req);
    UserResponse createUser(UserCreationRequest userRequest);
//    UserResponse getMyInfor();


    UserResponse updateUser(UserUpdateRequest request);
    void deleteUser(String userId);
    List<UserResponse> getAllUsers();
    UserResponse getUser(String userId);
    User getUserByEmail(String email);
    void resetPassword(String email, String newPassword);

    String handlePreRegister(@Valid UserCreationRequest request) throws JsonProcessingException;

    UserResponse handleConfirmRegister(@Valid ConfirmOtpRequest confirmRequest) throws JsonProcessingException;
}
