package org.balllog.backend.jwt.repository;

import org.balllog.backend.jwt.entity.JwtRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, Long> {
    Optional<JwtRefreshToken> findFirstByUserId(Long userId);

    void deleteByUserId(Long userId);
}
