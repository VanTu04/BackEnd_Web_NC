package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Dto.Requests.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.UserMapper;
import com.vawndev.spring_boot_readnovel.Repositories.RoleRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public List<UserResponse> getAllUsers() {
        return List.of();
    }

    @Override
    public UserResponse getUser(String userId) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
    }


}
