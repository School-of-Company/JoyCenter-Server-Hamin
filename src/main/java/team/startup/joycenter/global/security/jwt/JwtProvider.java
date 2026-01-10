package team.startup.joycenter.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.startup.joycenter.global.security.exception.ExpiredTokenException;
import team.startup.joycenter.global.security.exception.InvalidTokenException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private static final long ACCESS_TOKEN_TIME = 60L * 60 * 24 * 1000;
    public static final long REFRESH_TOKEN_TIME = 60L * 60 * 24 * 7 * 1000;

    public Key getAccessKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getAccessSecret().getBytes(StandardCharsets.UTF_8));
    }

    public Key getRefreshKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long memberId) {
        Date expiry = new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(getAccessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long memberId) {
        Date expiry = new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setExpiration(expiry)
                .signWith(getRefreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getAccessKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getRefreshKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public Long getSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getRefreshKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Long subject = Long.valueOf(claims.getSubject());
        return subject;
    }

    public long getAccessTokenTime() {
        return ACCESS_TOKEN_TIME;
    }

    public long getRefreshTokenTime() {
        return REFRESH_TOKEN_TIME;
    }
}
