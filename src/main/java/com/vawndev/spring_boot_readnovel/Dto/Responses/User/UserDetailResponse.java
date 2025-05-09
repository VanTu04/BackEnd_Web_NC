package com.vawndev.spring_boot_readnovel.Dto.Responses.User;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDetailResponse extends UserResponse {
    private String createdAt;
    private String updatedAt;
    private String deleteAt;
    private boolean isActive;
    private boolean isRequest;
}
