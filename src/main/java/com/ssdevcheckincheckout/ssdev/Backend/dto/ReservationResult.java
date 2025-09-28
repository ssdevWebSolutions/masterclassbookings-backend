package com.ssdevcheckincheckout.ssdev.Backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationResult {
    private boolean success;
    private String reservationId;
    private LocalDateTime expiresAt;
    private long ttlSeconds;
    private String errorMessage;
    private List<Long> unavailableSessionIds;
    
    // Constructors
    private ReservationResult(boolean success) {
        this.success = success;
    }
    
    public static ReservationResult success(String reservationId, LocalDateTime expiresAt, long ttlSeconds) {
        ReservationResult result = new ReservationResult(true);
        result.reservationId = reservationId;
        result.expiresAt = expiresAt;
        result.ttlSeconds = ttlSeconds;
        return result;
    }
    
    public static ReservationResult failure(String errorMessage, List<Long> unavailableSessionIds) {
        ReservationResult result = new ReservationResult(false);
        result.errorMessage = errorMessage;
        result.unavailableSessionIds = unavailableSessionIds;
        return result;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getReservationId() { return reservationId; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public long getTtlSeconds() { return ttlSeconds; }
    public String getErrorMessage() { return errorMessage; }
    public List<Long> getUnavailableSessionIds() { return unavailableSessionIds; }
}
