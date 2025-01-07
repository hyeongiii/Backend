package org.balllog.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.balllog.backend.auth.userinfo.KakaoUserInfo;
import org.balllog.backend.global.exception.GeneralException;
import org.balllog.backend.global.response.Code;
import org.balllog.backend.jwt.dto.TokenDto;
import org.balllog.backend.jwt.service.JwtTokenService;
import org.balllog.backend.user.entity.KakaoAccount;
import org.balllog.backend.user.entity.User;
import org.balllog.backend.user.repository.KakaoAccountRepository;
import org.balllog.backend.user.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private final JwtTokenService tokenService;
    private final KakaoAccountRepository kakaoAccountRepository;
    private final UserRepository userRepository;

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private final JwtTokenService jwtTokenService;

    @Transactional
    public TokenDto login(String providerAccessToken) {
        Optional<User> user = Optional.empty();

        KakaoUserInfo kakaoUserInfo = validateKakaoToken(providerAccessToken);

        user = kakaoAccountRepository.findById(kakaoUserInfo.getId())
                .map(KakaoAccount::getUser);

        if (user.isEmpty()) {
            return signUpFromKakaoUserInfo(kakaoUserInfo);
        }

        return tokenService.issueToken(user.get());
    }

    private KakaoUserInfo validateKakaoToken(String providerAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(providerAccessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                KAKAO_API_URL,
                HttpMethod.GET,
                request,
                KakaoUserInfo.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new GeneralException(Code.INVALID_KAKAO_TOKEN);
        }
    }

    private TokenDto signUpFromKakaoUserInfo(KakaoUserInfo kakaoUserInfo) {
        User user = new User(kakaoUserInfo.getId().toString(), User.SocialType.KAKAO, User.UserRole.USER);
        User savedUser = userRepository.save(user);
        kakaoAccountRepository.save(
                KakaoAccount.builder()
                        .id(kakaoUserInfo.getId())
                        .user(savedUser)
                        .build()
        );

        return jwtTokenService.issueToken(savedUser);
    }

}
