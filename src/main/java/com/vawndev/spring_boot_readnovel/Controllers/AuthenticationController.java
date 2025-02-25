package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.AuthenticationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.AuthenticationResponse;
import com.vawndev.spring_boot_readnovel.Services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Value("${jwt.refreshable-duration}")
    private long refreshableDuration;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshableDuration)
                .path("/")
                .build();
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
    public ResponseEntity<ApiResponse<AuthenticationResponse>> getAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return null;
    }


    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@CookieValue(name = "refresh_token") String refreshToken) {
return null;
//        var result = authenticationService.refreshToken(request);
//        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }
//
//    @PostMapping("/logout")
//    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
//        authenticationService.logout(request);
//        return ApiResponse.<Void>builder().build();
//    }
}
