package org.balllog.backend.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int kboTeamId;

    @Column(unique = true, nullable = false)
    private String socialId;

    private LoginType loginType;

    private String password;

    private String email;

    private String name;

    private String imageUrl;

    private int level;

    private Status status;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    private LocalDateTime deletedAt;

    public enum LoginType {
        KAKAO(1, "카카오"),
        APPLE(2, "애플");

        private int type;
        private String description;

        LoginType(int socialType, String description) {}
    }

    public enum Status {
        ACTIVE(1, "활성 회원"),
        INACTIVE(2, "비활성 회원"),
        SUSPENDED(3, "정지된 회원"),
        BANNED(4, "차단된 회원"),
        DELETED(100, "탈퇴한 회원");

        private int value;
        private String description;

        Status(int value, String description) {}
    }

    @Builder
    public User(String socialId, LoginType loginType, UserRole role) {
        this.socialId = socialId;
        this.loginType = loginType;
        this.role = role != null ? role : UserRole.USER;
        this.status = Status.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

   public enum UserRole {
        USER, ADMIN
   }

}
