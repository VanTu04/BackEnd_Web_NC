package com.vawndev.spring_boot_readnovel.Configurations;

import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userService.getUserByEmail(username);

        List<SimpleGrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                roles
        );

    }
}
