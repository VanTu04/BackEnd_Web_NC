package com.vawndev.spring_boot_readnovel.Services.Impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.UserMapper;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.CloundService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CloundService cloundService;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserUpdateRequest updateRequest;
    private UserResponse userResponse;
    private MockMultipartFile mockImageFile;

    @BeforeEach
    void setUp() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // Setup mock user
        mockUser = User.builder()
                .id("1639b986-96bf-440c-a682-837688a4350a")
                .email("test@example.com")
                .fullName("Ml Anhem")
                .password("Anmisoi12")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        // Setup update request
        updateRequest = UserUpdateRequest.builder()
                .password("$2a$10$KraqPK.qJUWYG3dMXYnrcuOJk/zikRGdYTDaKdPk2bvHdZqFk3J8G")
                .fullName("Ml Anhem Updated")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        // Setup mock response
        userResponse = UserResponse.builder()
                .id(mockUser.getId())
                .fullName(updateRequest.getFullName())
                .dateOfBirth(updateRequest.getDateOfBirth())
                .build();
    }

    @Test
    void updateUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
        });

        // Act
        UserResponse result = userService.updateUser(updateRequest);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(updateRequest.getFullName(), result.getFullName(), "Full name should match");
        assertEquals(updateRequest.getDateOfBirth(), result.getDateOfBirth(), "Date of birth should match");

        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
    }

    @Test
    void updateUser_InvalidPassword() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> userService.updateUser(updateRequest));
        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    void updateUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> userService.updateUser(updateRequest));
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Object User not found", exception.getMessage()); // Cập nhật thông báo lỗi
    }

    @Test
    void updateUser_NoChanges() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
        });

        // Act
        UserResponse result = userService.updateUser(updateRequest);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(mockUser.getFullName(), result.getFullName(), "Full name should match");
        assertEquals(mockUser.getDateOfBirth(), result.getDateOfBirth(), "Date of birth should match");

        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
    }

    @Test
    void updateUser_UpdateFullNameOnly() {
        // Arrange
        updateRequest.setFullName("New Full Name");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            mockUser.setFullName(savedUser.getFullName()); // Cập nhật tên đầy đủ trong mockUser
            return mockUser;
        });
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
        });

        // Act
        UserResponse result = userService.updateUser(updateRequest);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("New Full Name", result.getFullName(), "Full name should match");
        assertEquals(mockUser.getDateOfBirth(), result.getDateOfBirth(), "Date of birth should match");

        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
    }

    @Test
    void updateUser_UpdateDateOfBirthOnly() {
        // Arrange
        updateRequest.setDateOfBirth(LocalDate.of(2000, 1, 1)); // Cập nhật ngày sinh mới
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(updateRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            mockUser.setDateOfBirth(savedUser.getDateOfBirth()); // Cập nhật ngày sinh trong mockUser
            return mockUser;
        });
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
        });

        // Act
        UserResponse result = userService.updateUser(updateRequest);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(mockUser.getFullName(), result.getFullName(), "Full name should match");
        assertEquals(LocalDate.of(2000, 1, 1), result.getDateOfBirth(), "Date of birth should match");

        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
    }

    private ChapterRepository chapterRepository;
    
}


