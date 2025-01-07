package org.balllog.backend.jwt.service;

import lombok.RequiredArgsConstructor;
import org.balllog.backend.auth.userdetails.BallLogUserDetails;
import org.balllog.backend.global.exception.GeneralException;
import org.balllog.backend.global.response.Code;
import org.balllog.backend.jwt.dto.TokenDto;
import org.balllog.backend.jwt.dto.TokenRequestDto;
import org.balllog.backend.jwt.entity.JwtRefreshToken;
import org.balllog.backend.jwt.provider.JwtTokenProvider;
import org.balllog.backend.jwt.repository.JwtRefreshTokenRepository;
import org.balllog.backend.user.entity.User;
import org.balllog.backend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtTokenProvider tokenProvider;
    private final JwtRefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenDto issueToken(User user) {
        refreshTokenRepository.findFirstByUserId(user.getId()).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush();

        BallLogUserDetails userDetails = new BallLogUserDetails(user);
        TokenDto tokenDto = tokenProvider.generateToken(userDetails);

        JwtRefreshToken refreshToken = JwtRefreshToken.builder()
                .user(user)
                .refreshToken(tokenDto.getRefreshToken())
                .build();

        return tokenDto;
    }

    public TokenDto reissueByRefreshToken(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new GeneralException(Code.INVALID_REFRESH_TOKEN, "Refresh Token이 없습니다. 다시 로그인해주세요.");
        }

        // 2. Access Token 에서 UserId 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        Long userId = Long.parseLong(authentication.getName());

        JwtRefreshToken jwt = refreshTokenRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new GeneralException(Code.REFRESH_TOKEN_NOT_FOUND, "Refresh Token이 없습니다. 다시 로그인해주세요."));


        refreshTokenRepository.delete(jwt);
        refreshTokenRepository.flush();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        BallLogUserDetails userDetails = new BallLogUserDetails(user);

        TokenDto tokenDto = tokenProvider.generateToken(userDetails);
        JwtRefreshToken newJwt = JwtRefreshToken.builder()
                .user(user)
                .refreshToken(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(newJwt);

        return tokenDto;
    }
}
