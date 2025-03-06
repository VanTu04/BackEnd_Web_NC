package com.vawndev.spring_boot_readnovel.Dto.Responses.User;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    String name;
    String description;
}
