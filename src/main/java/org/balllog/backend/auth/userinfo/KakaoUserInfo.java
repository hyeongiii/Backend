package org.balllog.backend.auth.userinfo;

import lombok.Getter;

@Getter
public class KakaoUserInfo {
    Long id;
    KakaoAccount kakao_account;

    public static class KakaoAccount {
        private Profile profile;
    }

    @Getter
    public static class Profile {
        private String nickname;
    }
}
