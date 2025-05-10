package com.vawndev.spring_boot_readnovel.Dto.Responses.User;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminUserDetailResponse extends UserResponse {
    private String id ;
    private String fullName ;
    private String email ;
    private String createdAt ;

    private String updatedAt ;

    private String deleteAt = null;

    private boolean isActive;
    private boolean isRequest;
}
