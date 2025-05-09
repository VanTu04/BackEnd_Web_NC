package com.vawndev.spring_boot_readnovel.Utils.Help;

import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenHelper {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = null;
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaim("sub");
            return email;
        }

        return email;

    }

    public String getTokenInfo(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.MISS_TOKEN);
        }
        return bearerToken.substring(7);
    }

    public User getRealAuthorizedUser(String email, String bearerToken) {
        try {
            String token = getTokenInfo(bearerToken);
            User user = jwtUtils.validToken(token);

            User author = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            if (!user.getEmail().equals(author.getEmail())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            return author;
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public User getUserO2Auth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "You do not have permission to access this resource");
        }
        String email = null;
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaim("sub");
        }

        if (email == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}