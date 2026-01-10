package team.startup.joycenter.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.startup.joycenter.domain.auth.dto.response.TokenResponse;
import team.startup.joycenter.domain.auth.service.ReissueTokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final ReissueTokenService reissueTokenService;

    @PatchMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestHeader("RefreshToken")String refreshToken, HttpServletResponse response) {
        TokenResponse tokenResponse = reissueTokenService.execute(refreshToken, response);
        return ResponseEntity.ok(tokenResponse);
    }
}
