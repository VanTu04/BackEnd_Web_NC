package com.vawndev.spring_boot_readnovel.Dto.Responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
}