package com.vawndev.spring_boot_readnovel.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "otps")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp extends BaseEntity{
    private String email;
    private String otp;
    private LocalDateTime expirationTime;
}