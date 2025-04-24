package com.vawndev.spring_boot_readnovel.Dto.Requests.User;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    private String password;       // Mật khẩu hiện tại để xác thực
    private String fullName;       // Tên đầy đủ
    private LocalDate dateOfBirth; // Ngày tháng năm sinh
    private String imageUrl;       // URL ảnh đại diện
    //private List<String> roles;
}
