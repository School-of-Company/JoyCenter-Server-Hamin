package team.startup.joycenter.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team.startup.joycenter.domain.auth.dto.request.OAuthRequest;
import team.startup.joycenter.domain.auth.dto.response.TokenResponse;
import team.startup.joycenter.domain.auth.entity.RefreshToken;
import team.startup.joycenter.domain.auth.exception.AuthenticationFailedException;
import team.startup.joycenter.domain.auth.exception.Oauth2AuthorizationFailedException;
import team.startup.joycenter.domain.auth.repository.RefreshTokenRepository;
import team.startup.joycenter.domain.auth.service.OAuthService;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.member.repository.MemberRepository;
import team.startup.joycenter.global.security.jwt.JwtProvider;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenResponseClient;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

    @Override
    public TokenResponse execute(OAuthRequest request) {
        String decodedCode = URLDecoder.decode(request.code(), StandardCharsets.UTF_8);

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(request.provider());
        if (clientRegistration == null) {
            throw new Oauth2AuthorizationFailedException();
        }

        String redirectUri = clientRegistration.getRedirectUri();

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest
                .authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(redirectUri)
                .scopes(clientRegistration.getScopes())
                .build();

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse
                .success(decodedCode)
                .redirectUri(redirectUri)
                .build();

        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
        OAuth2AuthorizationCodeGrantRequest tokenRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, exchange);
        var tokenResponse = tokenResponseClient.getTokenResponse(tokenRequest);

        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken());
        OAuth2User oauth2User = oauth2UserService.loadUser(userRequest);

        String email = extractEmail(request.provider(), oauth2User.getAttributes());
        if (email == null) {
            throw new AuthenticationFailedException();
        }

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> createNewMember(email, clientRegistration.getClientId(), request.provider()));

        String accessToken = jwtProvider.generateAccessToken(member.getId());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId());

        refreshTokenRepository.save(RefreshToken.builder()
                .memberId(member.getId())
                .token(refreshToken)
                .build());

        return new TokenResponse(
                accessToken,
                refreshToken,
                LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenTime()),
                LocalDateTime.now().plusSeconds(jwtProvider.getRefreshTokenTime())
        );
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Object kakaoAccountObj = attributes.get("kakao_account");
            if (kakaoAccountObj instanceof Map) {
                Map<?, ?> kakaoAccount = (Map<?, ?>) kakaoAccountObj;
                return (String) kakaoAccount.get("email");
            }
            return null;
        }
        return (String) attributes.get("email");
    }

    private Member createNewMember(String email, String providerId, String provider) {
        return memberRepository.save(Member.builder()
                .email(email)
                .providerId(providerId)
                .provider(provider)
                .build());
    }
}