package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.ConfirmOtpRequest;
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
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Services.OtpService;
import com.vawndev.spring_boot_readnovel.Services.UserService;
import com.vawndev.spring_boot_readnovel.Utils.AesEncryptionUtil;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final ObjectMapper objectMapper;
    private final AesEncryptionUtil aesEncryptionUtil;
    private final TokenHelper tokenHelper;

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public PageResponse<UserDetailReponse> getAllUser(PageRequest req) {
        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailReponse> userDetailReponseList = users.getContent().stream().map(user -> UserDetailReponse
                .builder()
                .id(user.getId())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt() != null ? TimeZoneConvert.convertUtcToUserTimezone(user.getCreatedAt()): null)//user.getCreatedAt() != null ? 
                .updatedAt(user.getUpdatedAt() != null ? TimeZoneConvert.convertUtcToUserTimezone(user.getUpdatedAt()):null)//user.getUpdatedAt() != null ? 
                .deleteAt(user.getDeleteAt() != null ? TimeZoneConvert.convertUtcToUserTimezone(user.getDeleteAt())
                        : null)
                .isActive(user.isActive())
                .isRequest(user.isRequest())
                .build()).collect(Collectors.toList());
        return PageResponse.<UserDetailReponse>builder().page(req.getPage()).limit(req.getLimit())
                .data(userDetailReponseList).total(users.getTotalPages()).build();

    }

    @Override
    public UserResponse createUser(UserCreationRequest userRequest) {
        User user = userMapper.toUser(userRequest);

        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findByName(PredefinedRole.CUSTOMER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);
        user.setImageUrl(
                "https://res.cloudinary.com/dxpyuj1mm/image/upload/v1745306533/covers/ghblsj9e1exvmcozoewf.png");

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

    @Override
    public void resetPassword(String email, String newPassword) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Encode the new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Save the updated user back to the repository
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.ERROR_SAVE_DATA, "Error saving the new password");
        }
    }

    @Override
    public String handlePreRegister(UserCreationRequest request) throws JsonProcessingException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (!request.isPasswordMatching()) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        String jsonData = objectMapper.writeValueAsString(request);
        String encrypted = aesEncryptionUtil.encrypt(jsonData);

        otpService.sendOtp(request.getEmail());
        return encrypted;
    }

    @Override
    public UserResponse handleConfirmRegister(ConfirmOtpRequest confirmRequest) throws JsonProcessingException {
        String encrypted = confirmRequest.getEncryptedData();
        String otp = confirmRequest.getOtp();

        String decryptedJson = aesEncryptionUtil.decrypt(encrypted);
        UserCreationRequest request = objectMapper.readValue(decryptedJson, UserCreationRequest.class);

        if (!otpService.validateOtp(request.getEmail(), otp)) {
            throw new AppException(ErrorCode.INVALID, "OTP is invalid");
        }
        return this.createUser(request);
    }

    private final CloundService cloundService;

    @Override
    public UserResponse updateUser(UserUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "Current password is incorrect.");
        }

        // Cập nhật thông tin cá nhân
        if (!request.getFullName().equals(user.getFullName())) {
            user.setFullName(request.getFullName());
        }
        if (!request.getDateOfBirth().equals(user.getDateOfBirth())) {
            user.setDateOfBirth(request.getDateOfBirth()); // Đảm bảo ngày sinh được cập nhật
        }

        // Lưu thông tin người dùng đã cập nhật
        userRepository.save(user);

        // Trả về thông tin người dùng đã cập nhật
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getProfile() {
        User user = tokenHelper.getUserO2Auth();
        return UserResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .balance(user.getBalance())
                .build();
    }

}
