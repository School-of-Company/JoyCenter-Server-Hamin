package team.startup.joycenter.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.joycenter.domain.auth.dto.request.OAuthRequest;
import team.startup.joycenter.domain.auth.dto.response.TokenResponse;
import team.startup.joycenter.domain.auth.service.OAuthService;
import team.startup.joycenter.domain.auth.service.ReissueTokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuthService oAuthService;
    private final ReissueTokenService reissueTokenService;

    @Operation(
            summary = "OAuth 로그인",
            description = """
                    프론트에서 받은 authorization code와 redirectUri를 서버로 전달하면,
                    서버가 provider 토큰 교환 + userinfo 조회 후 토큰을 발급합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인/회원가입 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
    })
    @PostMapping
    public ResponseEntity<TokenResponse> oauth(@RequestBody OAuthRequest request) {
        TokenResponse response = oAuthService.execute(request);
        return ResponseEntity.ok(response);
    }

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
            @RequestHeader("RefreshToken")String refreshToken) {
        TokenResponse tokenResponse = reissueTokenService.execute(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
