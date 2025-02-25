package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity{
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_request")
    private boolean isRequest;

    @Column(name = "refresh_token")
    @Lob
    private String refreshToken;

    @ManyToMany
    Set<Role> roles;
}
