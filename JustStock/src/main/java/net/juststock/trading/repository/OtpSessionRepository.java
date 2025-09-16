package net.juststock.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.juststock.trading.domain.common.OtpSession;

import java.util.Optional;

public interface OtpSessionRepository extends JpaRepository<OtpSession, Long> {
    Optional<OtpSession> findTopByMobileNumberOrderByExpiresAtDesc(String mobileNumber);
    long deleteByMobileNumber(String mobileNumber);
}
