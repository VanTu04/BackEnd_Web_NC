package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserDetailReponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.UserMapper;
import com.vawndev.spring_boot_readnovel.Repositories.RoleRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.UserService;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public PageResponse<UserDetailReponse> getAllUser(PageRequest req) {
        Pageable pageable= PaginationUtil.createPageable(req.getPage(), req.getLimit());
        Page<User> users=userRepository.findAll(pageable);
        List<UserDetailReponse> userDetailReponseList = users.getContent().stream().map(user->
                UserDetailReponse
                        .builder()
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .createdAt(TimeZoneConvert.convertUtcToUserTimezone(user.getCreatedAt()))
                        .updatedAt(TimeZoneConvert.convertUtcToUserTimezone(user.getUpdatedAt()))
                        .deleteAt(!user.getDeleteAt().equals(null) ? TimeZoneConvert.convertUtcToUserTimezone(user.getDeleteAt()) : null )
                        .build()
                ).collect(Collectors.toList());
        PageResponse.<UserDetailReponse>builder().page(req.getPage()).limit(req.getLimit()).data(userDetailReponseList).total(users.getTotalPages()).build();
        return null;
    }

    @Override
    public UserResponse createUser(UserCreationRequest userRequest) {
        User user = userMapper.toUser(userRequest);

        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findByName(PredefinedRole.CUSTOMER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.OBJECT_EXISTED, "User");
        }

        return userMapper.toUserResponse(user);
    }

    private User findUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_EXISTED));
    }

    @Override
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteUser(String userId) {
        var user = this.findUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse getUser(String userId) {
        var user = this.findUserById(userId);
        return userMapper.toUserResponse(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_EXISTED, "User"));
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_EXISTED,"User")); 

        // Encode the new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Save the updated user back to the repository
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.ERROR_SAVE_DATA, "Error saving the new password");
        }
    }



}
