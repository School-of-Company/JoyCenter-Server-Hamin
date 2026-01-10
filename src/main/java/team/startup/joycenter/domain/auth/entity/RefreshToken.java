package team.startup.joycenter.domain.auth.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import team.startup.joycenter.global.security.jwt.JwtProvider;

@RedisHash(value = "joycenter_refreshToken", timeToLive = JwtProvider.REFRESH_TOKEN_TIME)
@Getter
@Builder
public class RefreshToken {

    @Id
    private Long memberId;

    @Indexed
    private String token;
}
