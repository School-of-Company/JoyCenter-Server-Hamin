package team.startup.joycenter.global.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.member.repository.MemberRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId();

        String userNameAttributeName = request.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> raw = oAuth2User.getAttributes();
        Map<String, Object> attributes = new HashMap<>();

        String providerId = null;
        Object rawProviderId = raw.get(userNameAttributeName);

        if (rawProviderId == null && "kakao".equals(provider)) {
            rawProviderId = raw.get("id");
        }

        if (rawProviderId != null) {
            providerId = String.valueOf(rawProviderId);
        }

        String email = null;


        if ("google".equals(provider)) {
            email = (String) raw.get("email");
        }

        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) raw.get("kakao_account");

            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
            }
        }

        if (providerId == null) {
            throw new IllegalArgumentException("providerId is null");
        }

        final String finalProvider = provider;
        final String finalProviderId = providerId;
        final String finalEmail = email;

        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .provider(finalProvider)
                                .providerId(finalProviderId)
                                .email(finalEmail)
                                .build()
                ));

        attributes.put("memberId", member.getId());
        if (email != null) {
            attributes.put("email", email);
        }

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "memberId"
        );
    }
}
