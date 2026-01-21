package team.startup.joycenter.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.auth.dto.response.TokenResponse;
import team.startup.joycenter.domain.auth.entity.RefreshToken;
import team.startup.joycenter.domain.auth.exception.UnauthorizedException;
import team.startup.joycenter.domain.auth.repository.RefreshTokenRepository;
import team.startup.joycenter.domain.auth.service.ReissueTokenService;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.member.exception.NotFoundMemberException;
import team.startup.joycenter.domain.member.repository.MemberRepository;
import team.startup.joycenter.global.security.jwt.JwtProvider;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReissueTokenServiceImpl implements ReissueTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public TokenResponse execute(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException();
        }

        RefreshToken saved = refreshTokenRepository.findByMemberId(jwtProvider.getSubject(refreshToken))
                .orElseThrow(UnauthorizedException::new);

        if (!refreshToken.equals(saved.getToken())) {
            refreshTokenRepository.delete(saved);
            throw new UnauthorizedException();
        }

        Member member = memberRepository.findById(jwtProvider.getSubject(refreshToken))
                .orElseThrow(NotFoundMemberException::new);

        String newAccessToken = jwtProvider.generateAccessToken(member.getId());
        String newRefreshToken = jwtProvider.generateRefreshToken(member.getId());

        RefreshToken updatedToken = RefreshToken.builder()
                .memberId(saved.getMemberId())
                .token(newRefreshToken)
                .build();

        refreshTokenRepository.save(updatedToken);

        return new TokenResponse(
                newAccessToken,
                newRefreshToken,
                LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenTime()),
                LocalDateTime.now().plusSeconds(jwtProvider.getRefreshTokenTime())
        );
    }
}
