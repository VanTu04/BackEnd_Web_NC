package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.nimbusds.jose.util.Base64;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Auth.AuthenticationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Auth.AuthenticationResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.UserMapper;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserMapper userMapper;

    @Value("${jwt.valid-duration}")
    private long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;

    @Value("${jwt.signer-key}")
    private String SIGNER_KEY;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // kiem tra user co trong db khong
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // kiem tra mat khau
            boolean valid = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if(!valid) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

        // kiem tra tinh trang tai khoan
        if(!user.isActive()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // nạp username và password vào security để tạo một authentication object
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        // xác thực người dùng
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // đưa thông tin xác thực người dùng đã đăng nhập vào Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var token = generateToken(user);
        var refreshToken = generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public ResponseCookie createRefreshTokenCookie(String refreshToken, long seconds) {
        return ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(seconds)
                .path("/")
                .build();
    }

    private SecretKey getSecretAccessKey() {
        byte[] keyBytes = Base64.from(SIGNER_KEY).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, MacAlgorithm.HS512.getName());
    }

    private User validToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(getSecretAccessKey())
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
        try {
            Jwt decode = jwtDecoder.decode(token);
            String email = decode.getSubject();
            return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
        } catch (Exception e){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }
    @Override
    public AuthenticationResponse generateTokenByRefreshToken(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException(ErrorCode.MISS_TOKEN);
        }
        User user = validToken(refreshToken);
        if(!user.getRefreshToken().equals(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        String refreshNewToken = generateRefreshToken(user);
        user.setRefreshToken(refreshNewToken);
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(generateToken(user))
                .refreshToken(refreshNewToken)
                .build();
    }

    @Override
    public ResponseCookie logout(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException(ErrorCode.MISS_TOKEN);
        }
        User user = validToken(refreshToken);
        if(!user.getRefreshToken().equals(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        user.setRefreshToken(null);
        userRepository.save(user);

        SecurityContextHolder.clearContext();
        return createRefreshTokenCookie("", 0);
    }


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

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
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

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }
}
