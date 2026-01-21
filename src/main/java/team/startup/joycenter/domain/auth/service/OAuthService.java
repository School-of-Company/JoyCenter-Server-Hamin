package team.startup.joycenter.domain.auth.service;

import team.startup.joycenter.domain.auth.dto.request.OAuthRequest;
import team.startup.joycenter.domain.auth.dto.response.TokenResponse;

public interface OAuthService {
    TokenResponse execute(OAuthRequest request);
}
