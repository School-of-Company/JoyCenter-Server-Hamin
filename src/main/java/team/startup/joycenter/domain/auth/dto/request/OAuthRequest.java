package team.startup.joycenter.domain.auth.dto.request;

public record OAuthRequest(
        String code,
        String redirectUri,
        String provider
) {
}
