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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final TokenParser tokenParser;
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

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
                                        "/login/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                // auth
                                .requestMatchers(HttpMethod.POST, "/api/auth").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/api/auth/reissue").permitAll()

                                // post
                                .requestMatchers(HttpMethod.POST, "/api/post").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/post/{postId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/post/all").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/post/{postId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/post/{postId}").authenticated()

                                // attachments
                                .requestMatchers(HttpMethod.POST, "/api/attachments").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/attachments/{attachmentsId}").authenticated()
                                .anyRequest()
                                .denyAll()

                )

                        .addFilterBefore(new ExceptionFilter(objectMapper), UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(new JwtFilter(jwtProvider, tokenParser), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
