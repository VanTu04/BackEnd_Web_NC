package com.vawndev.spring_boot_readnovel.Utils.Help;

import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenHelper {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

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
        }catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

}
