package org.balllog.backend.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class KakaoAccount {
    @Id
    @Column
    Long id;

    @Column(nullable = false)
    @Setter
    private String refreshToken;

    @OneToOne
    @JoinColumn
    private User user;

    @Builder
    public KakaoAccount(Long id, String refreshToken, User user) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.user = user;
    }

}
