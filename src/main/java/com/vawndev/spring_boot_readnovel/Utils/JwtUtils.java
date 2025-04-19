package com.vawndev.spring_boot_readnovel.Utils;

import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Value("${jwt.valid-duration}")
    private long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;

    @Value("${jwt.signer-key}")
    private String SIGNER_KEY;

    public String generateRefreshToken(User user) {
        // header jwt
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512).build();

        // payload jwt
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuer("vawndev.com")
                .expiresAt(new Date(
                        Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ).toInstant())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }

    public String generateToken(User user) {
        // header jwt
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512).build();

        String authorities = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        // payload jwt
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuer("vawndev.com")
                .expiresAt(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ).toInstant())
                .claim("scope", authorities)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }

    public User validToken(String token) {
        try {
            Jwt decode = jwtDecoder.decode(token);
            String email = decode.getSubject();
            return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
        } catch (Exception e){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken, long seconds) {
        return ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(seconds)
                .path("/")
                .build();
    }
}
