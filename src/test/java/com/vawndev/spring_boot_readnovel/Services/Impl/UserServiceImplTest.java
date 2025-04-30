package com.vawndev.spring_boot_readnovel.Services.Impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

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

import lombok.Builder;
import lombok.experimental.SuperBuilder;

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
                .id("1639b986-96bf-440c-a682-837688a4350a")
                .fullName("Ml Anhem")
                .password("Anmisoi12")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .imageUrl("https://example.com/new-image.jpg")
                .build();

        // Setup update request
        updateRequest = UserUpdateRequest.builder()
                .password("$2a$10$KraqPK.qJUWYG3dMXYnrcuOJk/zikRGdYTDaKdPk2bvHdZqFk3J8G")
                .fullName("Ml Anhem")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .imageUrl("https://example.com/new-image.jpg")
                .build();

        // Setup mock response
        userResponse = UserResponse.builder()
                .id(mockUser.getId())
                .fullName(mockUser.getFullName())
                .dateOfBirth(mockUser.getDateOfBirth())
                .imageUrl(mockUser.getImageUrl())
                .build();
    }

    @Test
    void updateUser_Success() {
        System.out.println("=== Starting updateUser_Success test ===");
        
        // Arrange
        System.out.println("Setting up mocks...");
        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);
        
        // Act
        System.out.println("Executing updateUser...");
        UserResponse result = userService.updateUser(updateRequest);
        
        // Assert
        System.out.println("Verifying results...");
        assertNotNull(result, "Result should not be null");
        assertEquals(updateRequest.getFullName(), result.getFullName(), "Full name should match");
        assertEquals(updateRequest.getDateOfBirth(), result.getDateOfBirth(), "Date of birth should match");
        assertEquals(updateRequest.getImageUrl(), result.getImageUrl(), "Image URL should match");
        
        System.out.println("Verifying mock interactions...");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
        
        System.out.println("=== Test completed successfully ===");
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
        when(userMapper.toUserResponse(any(User.class))).thenReturn(
            UserResponse.builder()
                .id(mockUser.getId())
                .fullName("Updated Name")
                .dateOfBirth(mockUser.getDateOfBirth())
                .imageUrl(mockUser.getImageUrl())
                .build()
        );

        // Act
        UserResponse result = userService.updateUser(partialRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
        assertEquals(mockUser.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(mockUser.getImageUrl(), result.getImageUrl());
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