package team.startup.joycenter.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import team.startup.joycenter.global.auth.MemberDetailsService;

@Component
@RequiredArgsConstructor
public class TokenParser {

    private final JwtProvider jwtProvider;
    private static final String BEARER_TYPE = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final MemberDetailsService memberDetailsService;

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtProvider.getAccessKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Authentication parseAuthentication(String token) {
        Claims claims = parseClaims(token);
        String memberId = claims.getSubject();
        UserDetails principal = memberDetailsService.loadUserByUsername(memberId);

        return new UsernamePasswordAuthenticationToken(
                principal,
                "",
                principal.getAuthorities()
        );
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(BEARER_TYPE.length());
        }
        return null;
    }
}
