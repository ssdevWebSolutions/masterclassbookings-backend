package com.ssdevcheckincheckout.ssdev.Backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingRequestDto {

    private boolean acceptDataPolicy;
    private boolean acceptTerms;
    private String bookingType;
    private Long childId;
    private Double discountApplied;
    private Long parentId;
    private Boolean paymentStatus;
    private List<Long> sessionIds;
    private LocalDateTime timestamp;
    private Double totalAmount;

    // Inner Classes (optional, for child details)
    public static class ChildDetails {
        private Long id;
        private String firstName;
        private String lastName;
        private int age;
        private String club;
        private String level;
        private String medicalInfo;
        // Getters & Setters
    }

    // --- Getters & Setters ---
    public boolean isAcceptDataPolicy() { return acceptDataPolicy; }
    public void setAcceptDataPolicy(boolean acceptDataPolicy) { this.acceptDataPolicy = acceptDataPolicy; }
    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }
    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }
    public Long getChildId() { return childId; }
    public void setChildId(Long childId) { this.childId = childId; }
    public Double getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(Double discountApplied) { this.discountApplied = discountApplied; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Boolean getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Boolean paymentStatus) { this.paymentStatus = paymentStatus; }
    public List<Long> getSessionIds() { return sessionIds; }
    public void setSessionIds(List<Long> sessionIds) { this.sessionIds = sessionIds; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}
