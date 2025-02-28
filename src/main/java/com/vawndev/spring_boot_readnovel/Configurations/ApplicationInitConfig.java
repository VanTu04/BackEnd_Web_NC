package com.vawndev.spring_boot_readnovel.Configurations;

import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Repositories.RoleRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;
    private static final String ADMIN_EMAIL = "admin123@gmail.com";
    private static final String ADMIN_PASSWORD = "123456";

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if(userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
                Role adminRole = roleRepository.save(
                        Role.builder()
                                .name(PredefinedRole.ADMIN_ROLE)
                                .description("Admin role")
                                .build()
                );
                roleRepository.save(
                        Role.builder()
                                .name(PredefinedRole.AUTHOR_ROLE)
                                .description("Author role")
                                .build()
                );
                roleRepository.save(
                        Role.builder()
                                .name(PredefinedRole.CUSTOMER_ROLE)
                                .description("Customer role")
                                .build()
                );

                var roles =new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .fullName("Admin")
                        .roles(roles)
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.info("Admin user created");
            }
            else {
                log.info("Admin user already exists");
            }
        };
    }
}
