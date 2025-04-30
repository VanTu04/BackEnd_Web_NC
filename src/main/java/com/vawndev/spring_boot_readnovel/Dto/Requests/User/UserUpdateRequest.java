package com.vawndev.spring_boot_readnovel.Dto.Requests.User;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotBlank
    private String password;       // Mật khẩu hiện tại để xác thực
    @NotBlank
    private String fullName;       // Tên đầy đủ
    @NotBlank
    private LocalDate dateOfBirth; // Ngày tháng năm sinh

    //private List<String> roles;
}
