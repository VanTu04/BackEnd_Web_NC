package com.vawndev.spring_boot_readnovel.Configurations;

import com.vawndev.spring_boot_readnovel.Services.CustomAuthorizationRequestResolver;
import com.vawndev.spring_boot_readnovel.Services.CustomOAuth2UserService;
import com.vawndev.spring_boot_readnovel.Services.OAuth2LoginFailureHandler;
import com.vawndev.spring_boot_readnovel.Services.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/users",
            "/auth/token",
            "/auth/introspect",
            "/auth/logout",
            "/auth/refresh",
            "/oauth2/authorization/google",
            "/auth/google/callback",
            "/payment/vn-pay-callback/**",
            "auth/google",
            "/story/detail/**",
            "/story/author/**",
            "/story",
            "/homepage",
            "/chapter/*",
            "/chapter/*/proxy",
            "/search/**",
            "/category",
            "/search",
            "/users/pre-register",
            "/users/confirm-register",
            "/users/forgot-password/request-otp",
            "/users/forgot-password/reset",
            "/users/forgot-password/validate",
            "comment/**",
    };

        private final CustomOAuth2UserService customOAuth2UserService;

        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

        private final ClientRegistrationRepository clientRegistrationRepository;

        private final JwtDecoder jwtDecoder;

        @Value("${url.frontend}")
        private String frontendUrl;

        @Value("${url.admin-frontend}")
        private String adminFrontendUrl;

    @Value("${url.app-frontend}")
    private String appFrontendUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                httpSecurity
                                .cors(c -> c.configurationSource(corsFilter()))
                                .authorizeHttpRequests(request -> request
                                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .authenticationEntryPoint(new CustomAuthenticationEntrypoint())
                                                .authenticationManagerResolver(request -> {
                                                        String path = request.getRequestURI();
                                                        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
                                                                if (path.matches(publicEndpoint.replace("**", ".*"))) {
                                                                        // Bỏ qua xác thực JWT cho endpoint public
                                                                        return authentication -> null;
                                                                }
                                                        }
                                                        // Dùng mặc định nếu không phải public
                                                        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(
                                                                        jwtDecoder);
                                                        provider.setJwtAuthenticationConverter(
                                                                        jwtAuthenticationConverter());
                                                        return provider::authenticate;
                                                }))
                                .oauth2Login(oauth2 -> oauth2
                                                .authorizationEndpoint(
                                                                endpoint -> endpoint.authorizationRequestResolver(
                                                                                new CustomAuthorizationRequestResolver(
                                                                                                clientRegistrationRepository,
                                                                                                "/oauth2/authorization")))
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oAuth2LoginSuccessHandler)
                                                .failureHandler(oAuth2LoginFailureHandler))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(new CustomAuthenticationEntrypoint())
                                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .formLogin(AbstractHttpConfigurer::disable)
                                .csrf(AbstractHttpConfigurer::disable);

                return httpSecurity.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
                jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

                return jwtAuthenticationConverter;
        }

        @Bean
        public CorsConfigurationSource corsFilter() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Thêm địa chỉ frontend cho phép
        corsConfiguration
                .setAllowedOrigins(List.of("http://localhost:2185", adminFrontendUrl, frontendUrl, appFrontendUrl));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        corsConfiguration.setAllowCredentials(true); // Quan trọng để gửi cookie và header Authorization
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Set-Cookie", "X-Requested-With"));

                UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
                urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

                return urlBasedCorsConfigurationSource;
        }

}
