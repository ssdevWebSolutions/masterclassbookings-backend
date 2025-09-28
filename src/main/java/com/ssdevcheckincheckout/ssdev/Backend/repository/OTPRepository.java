package com.ssdevcheckincheckout.ssdev.Backend.repository;



import com.ssdevcheckincheckout.ssdev.Backend.entity.OTP;
import com.ssdevcheckincheckout.ssdev.Backend.entity.OTPType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

    // Find the latest valid OTP for a specific email and type
    @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.otpType = :otpType AND o.isUsed = false AND o.expiresAt > :currentTime ORDER BY o.createdAt DESC")
    Optional<OTP> findLatestValidOTPByEmailAndType(
        @Param("email") String email, 
        @Param("otpType") OTPType otpType, 
        @Param("currentTime") LocalDateTime currentTime
    );

    // Find OTP by email, code, and type (for verification)
    @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.otpCode = :otpCode AND o.otpType = :otpType AND o.isUsed = false AND o.expiresAt > :currentTime")
    Optional<OTP> findValidOTPByEmailAndCodeAndType(
        @Param("email") String email, 
        @Param("otpCode") String otpCode, 
        @Param("otpType") OTPType otpType, 
        @Param("currentTime") LocalDateTime currentTime
    );

    // Delete all expired OTPs (cleanup job)
    @Modifying
    @Transactional
    @Query("DELETE FROM OTP o WHERE o.expiresAt <= :currentTime")
    void deleteExpiredOTPs(@Param("currentTime") LocalDateTime currentTime);

    // Delete all used OTPs (cleanup job)
    @Modifying
    @Transactional
    @Query("DELETE FROM OTP o WHERE o.isUsed = true")
    void deleteUsedOTPs();

    // Mark OTP as used
    @Modifying
    @Transactional
    @Query("UPDATE OTP o SET o.isUsed = true WHERE o.id = :id")
    void markOTPAsUsed(@Param("id") Long id);

    // Delete all OTPs for a specific email and type (useful when generating new OTP)
    @Modifying
    @Transactional
    @Query("DELETE FROM OTP o WHERE o.email = :email AND o.otpType = :otpType")
    void deleteOTPsByEmailAndType(@Param("email") String email, @Param("otpType") OTPType otpType);

    // Count valid OTPs for rate limiting
    @Query("SELECT COUNT(o) FROM OTP o WHERE o.email = :email AND o.otpType = :otpType AND o.createdAt >= :timeLimit")
    long countOTPsCreatedAfter(
        @Param("email") String email, 
        @Param("otpType") OTPType otpType, 
        @Param("timeLimit") LocalDateTime timeLimit
    );
}