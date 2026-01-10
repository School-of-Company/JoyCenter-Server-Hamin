package team.startup.joycenter.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.startup.joycenter.global.filter.ExceptionFilter;
import team.startup.joycenter.global.filter.JwtFilter;
import team.startup.joycenter.global.security.handler.JwtAccessDeniedHandler;
import team.startup.joycenter.global.security.handler.JwtAuthenticationEntryPoint;
import team.startup.joycenter.global.security.jwt.JwtProvider;
import team.startup.joycenter.global.security.jwt.TokenParser;
import team.startup.joycenter.global.security.oauth.CustomOAuth2UserService;
import team.startup.joycenter.global.security.oauth.OAuth2FailureHandler;
import team.startup.joycenter.global.security.oauth.OAuth2SuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final TokenParser tokenParser;
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .exceptionHandling(config ->
                        config.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .sessionManagement(config ->
                        config.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/",
                                        "/favicon.ico",
                                        "/error",
                                        "/oauth2/**",
                                        "/login/**"
                                ).permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/api/auth/reissue").permitAll()
                                .anyRequest()
                                .denyAll()

                )

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                        .addFilterBefore(new ExceptionFilter(objectMapper), UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(new JwtFilter(jwtProvider, tokenParser), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
