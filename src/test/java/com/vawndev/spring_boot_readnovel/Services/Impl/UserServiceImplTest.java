package com.vawndev.spring_boot_readnovel.Services.Impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.UserMapper;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private TokenHelper tokenHelper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserUpdateRequest updateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = User.builder()
                .id("test-id")
                .fullName("Original Name")
                .password("encoded-password")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .imageUrl("original-image.jpg")
                .build();

        // Setup update request
        updateRequest = UserUpdateRequest.builder()
                .password("correct-password")
                .fullName("Updated Name")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .imageUrl("new-image.jpg")
                .build();

        // Setup user response
        userResponse = UserResponse.builder()
                .id("test-id")
                .fullName("Updated Name")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .imageUrl("new-image.jpg")
                .build();
    }

    @Test
    void updateUser_Success() {
        // Arrange
        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        // Act
        UserResponse result = userService.updateUser(updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
        assertEquals(LocalDate.of(1995, 1, 1), result.getDateOfBirth());
        assertEquals("new-image.jpg", result.getImageUrl());

        verify(userRepository).save(mockUser);
        verify(userMapper).toUserResponse(mockUser);
    }

    @Test
    void updateUser_InvalidPassword() {
        // Arrange
        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> userService.updateUser(updateRequest));
        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    void updateUser_PartialUpdate() {
        // Arrange
        UserUpdateRequest partialRequest = UserUpdateRequest.builder()
                .password("correct-password")
                .fullName("Updated Name")
                .build();

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(passwordEncoder.matches(partialRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        // Act
        UserResponse result = userService.updateUser(partialRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
        // Original values should remain unchanged
        assertEquals(LocalDate.of(1990, 1, 1), mockUser.getDateOfBirth());
        assertEquals("original-image.jpg", mockUser.getImageUrl());
    }

    @Test
    void updateUser_UserNotAuthenticated() {
        // Arrange
        when(tokenHelper.getUserO2Auth()).thenThrow(new AppException(ErrorCode.UNAUTHORIZED));

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> userService.updateUser(updateRequest));
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }
}