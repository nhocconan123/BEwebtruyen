package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.EmailOtp;
import com.truyen.webtruyen.entity.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    Optional<EmailOtp> findTopByEmailAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    long countByEmailAndPurposeAndCreatedAtAfter(String email, OtpPurpose purpose, LocalDateTime after);
}

