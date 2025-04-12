package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Auth.AuthenticationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Auth.AuthenticationResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Services.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
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

    @GetMapping("/google")
    public String googleLogin(@AuthenticationPrincipal OAuth2User principal) {
        // Lấy thông tin người dùng từ Google
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");

        // In thông tin người dùng (hoặc lưu vào database)
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Picture: " + picture);

        return "Login successful! Welcome, " + name;
    }

}
