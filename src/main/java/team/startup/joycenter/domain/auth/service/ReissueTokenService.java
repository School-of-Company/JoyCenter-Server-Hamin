package team.startup.joycenter.domain.auth.service;

import team.startup.joycenter.domain.auth.dto.response.TokenResponse;

public interface ReissueTokenService {
    TokenResponse execute(String refreshToken);
}
