package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Repositories.RoleRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub"); // "sub" là Google user ID
        String picture = oAuth2User.getAttribute("picture");


        if (email == null || googleId == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_info", "Email or Google ID not found from OAuth2 provider", null),
                    "Email or Google ID not found from OAuth2 provider"
            );
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Tài khoản tồn tại nhưng không liên kết Google
            if (existingUser.getGoogleId() == null || !existingUser.getGoogleId().equals(googleId)) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("account_exists", "Account exists but not linked with Google", null),
                        "Account exists but not linked with Google"
                );
            }

            // Trả về user đã liên kết
            return buildOAuth2User(existingUser, oAuth2User);
        }

        // User chưa tồn tại → tạo mới
        Set<Role> roles = Set.of(roleRepository.findByName(PredefinedRole.CUSTOMER_ROLE)
                .orElseThrow(() -> new RuntimeException("Role not found: " + PredefinedRole.CUSTOMER_ROLE)));

        User newUser = User.builder()
                .email(email)
                .googleId(googleId)
                .imageUrl(picture)
                .fullName(name)
                .isActive(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(newUser);

        return buildOAuth2User(savedUser, oAuth2User);
    }

    private OAuth2User buildOAuth2User(User user, OAuth2User oAuth2User) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }



}
