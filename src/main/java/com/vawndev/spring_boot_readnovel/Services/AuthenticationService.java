package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Auth.AuthenticationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Auth.AuthenticationResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import org.springframework.http.ResponseCookie;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    UserResponse getAccount();

    ResponseCookie createRefreshTokenCookie(String refreshToken, long seconds);

    AuthenticationResponse generateTokenByRefreshToken(String refreshToken);

    ResponseCookie logout(String accessToken);
}
