package org.balllog.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.balllog.backend.auth.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {
    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/login")
    public ResponseEntity<> kakaoLogin() {

    }
}
