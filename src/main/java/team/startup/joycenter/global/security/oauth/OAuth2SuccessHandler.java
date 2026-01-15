package team.startup.joycenter.global.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team.startup.joycenter.domain.auth.entity.RefreshToken;
import team.startup.joycenter.domain.auth.repository.RefreshTokenRepository;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.member.exception.NotFoundMemberException;
import team.startup.joycenter.domain.member.repository.MemberRepository;
import team.startup.joycenter.global.security.jwt.JwtProvider;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Value("${app.oauth.success-url}") private String REDIRECT_URI;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Lax}")
    private String sameSite;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Long memberId = (Long) attributes.get("memberId");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        String accessToken = jwtProvider.generateAccessToken(member.getId());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId());

        refreshTokenRepository.save(RefreshToken.builder()
                .memberId(member.getId())
                .token(refreshToken)
                .build());

        response.setHeader("Authorization", "Bearer " + accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite(sameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.sendRedirect(REDIRECT_URI);
    }
}
