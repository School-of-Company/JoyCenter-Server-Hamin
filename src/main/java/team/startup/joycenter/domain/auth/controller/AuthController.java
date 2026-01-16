package team.startup.joycenter.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "토큰 재발급",
            description = "RefreshToken 헤더로 리프레시 토큰을 보내면 새로운 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료/유효하지 않음", content = @Content),
    })
    @PatchMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(
            @Parameter(
                    description = "리프레시 토큰 (예: Bearer 없이 토큰 문자열만)",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader("RefreshToken")String refreshToken, HttpServletResponse response) {
        TokenResponse tokenResponse = reissueTokenService.execute(refreshToken, response);
        return ResponseEntity.ok(tokenResponse);
    }
}
