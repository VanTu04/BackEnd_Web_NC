package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "users")
public class User extends BaseEntity{
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_request")
    private boolean isRequest;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "google_id")
    private String googleId;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roles;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Subscription subscription;
}
