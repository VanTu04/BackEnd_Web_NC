package com.vawndev.spring_boot_readnovel.Dto.Requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String password;
}