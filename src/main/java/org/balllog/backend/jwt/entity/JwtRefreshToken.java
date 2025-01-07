package org.balllog.backend.jwt.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.balllog.backend.global.entity.BaseTimeEntity;
import org.balllog.backend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class JwtRefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public JwtRefreshToken(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }
}
