package org.balllog.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.balllog.backend.auth.service.KakaoAuthService;
import org.balllog.backend.global.response.DataResponseDto;
import org.balllog.backend.global.response.ResponseDto;
import org.balllog.backend.jwt.dto.TokenDto;
import org.balllog.backend.jwt.dto.TokenRequestDto;
import org.balllog.backend.jwt.service.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;
    private final JwtTokenService tokenService;

    @PostMapping("/login")
    public DataResponseDto<TokenDto> kakaoLogin(
            @RequestHeader("Authorization") String providerAccessToken
    ) {
        return DataResponseDto.of(kakaoAuthService.login(providerAccessToken));
    }

    @PostMapping("/refresh")
    public DataResponseDto<TokenDto> refresh(@RequestBody TokenRequestDto tokenRequestDto) {
        return DataResponseDto.of(tokenService.reissueByRefreshToken(tokenRequestDto));
    }
}
