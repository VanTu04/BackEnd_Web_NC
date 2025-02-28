package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.AuthenticationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.AuthenticationResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.UserResponse;
import com.vawndev.spring_boot_readnovel.Services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        ResponseCookie responseCookie = authenticationService.createRefreshTokenCookie(result.getRefreshToken(), REFRESHABLE_DURATION);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ApiResponse.<AuthenticationResponse>builder().result(
                        AuthenticationResponse.builder()
                                .accessToken(result.getAccessToken())
                                .build())
                        .build()
                );
    }

    @GetMapping("/account")
    public ApiResponse<UserResponse> getAccountLogin() {
        var result = authenticationService.getAccount();
        return ApiResponse.<UserResponse>builder().result(result).build();
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        var result = authenticationService.generateTokenByRefreshToken(refreshToken);
        ResponseCookie responseCookie = authenticationService.createRefreshTokenCookie(result.getRefreshToken(), REFRESHABLE_DURATION);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ApiResponse.<AuthenticationResponse>builder().result(
                                AuthenticationResponse.builder()
                                        .accessToken(result.getAccessToken())
                                        .build())
                        .build()
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        ResponseCookie deleteCookie = authenticationService.logout(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.<String>builder().result("Success Logout").build());
    }

    @GetMapping("/google/success")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> googleLogin(OAuth2AccessToken token) {

        return null;
    }
}
