package com.vawndev.spring_boot_readnovel.Dto.Responses.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDetailReponse extends UserResponse{
    private String id ;
    private String fullName ;
    private String email ;
    private String createdAt ;

    private String updatedAt ;

    private String deleteAt = null;
}
