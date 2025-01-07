package org.balllog.backend.user.repository;

import org.balllog.backend.user.entity.KakaoAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoAccountRepository extends JpaRepository<KakaoAccount, Long> {
    Optional<KakaoAccount> findById(Long id);
}
