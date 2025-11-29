package com.xly.marry.repository;

import com.xly.marry.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findFirstByTargetAndTypeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String target, VerificationCode.Type type, LocalDateTime now);
    
    List<VerificationCode> findByTargetAndTypeAndCreatedAtAfter(
            String target, VerificationCode.Type type, LocalDateTime after);
}