package com.vawndev.spring_boot_readnovel.Dto.Responses.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDetailReponse extends UserResponse{
    private String createdAt ;

    private String updatedAt ;

    private String deleteAt = null;
}
