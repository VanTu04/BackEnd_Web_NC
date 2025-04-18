package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
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
import java.util.stream.Collectors;

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

    private User findUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
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
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
    }


}
