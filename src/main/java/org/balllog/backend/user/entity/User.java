package org.balllog.backend.user.entity;

import com.fasterxml.jackson.core.JsonToken;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balllog.backend.global.entity.BaseTimeEntity;
import org.balllog.backend.global.exception.GeneralException;
import org.balllog.backend.global.response.Code;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int kboTeamId;

    @Column(unique = true, nullable = false)
    private String socialId;

    private SocialType socialType;

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

    public enum SocialType {
        KAKAO("kakao", "카카오"),
        APPLE("apple", "애플");

        private String type;
        private String description;

        SocialType(String socialType, String description) {}

        public static SocialType fromString(String socialType) {
            for (SocialType st : SocialType.values()) {
                if (st.type.equalsIgnoreCase(socialType)) {
                    return st;
                }
            }
            throw new GeneralException(Code.SOCIAL_TYPE_NOT_FOUND);
        }
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
    public User(Long id, String socialId, SocialType socialType) {
        this.id = id;
        this.socialId = socialId;
        this.socialType = socialType;
    }

    public User(String socialId, SocialType socialType, UserRole role) {
        this.socialId = socialId;
        this.socialType = socialType;
        this.role = role != null ? role : UserRole.USER;
        this.status = Status.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

   public enum UserRole {
        USER, ADMIN
   }

}
