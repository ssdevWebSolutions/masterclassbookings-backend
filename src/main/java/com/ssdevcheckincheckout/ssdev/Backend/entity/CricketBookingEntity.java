package com.ssdevcheckincheckout.ssdev.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cricket_bookings")
public class CricketBookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId;
    private Long childId;

    private Double amount;            // individual booking amount
    private Boolean paymentStatus = false;
    private String bookingType;       // "individual_sessions" or "platinum_pass"
    private Double discountApplied = 0.0;
    private Double totalAmount;

    @ElementCollection
    @CollectionTable(name = "cricket_booking_sessions", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "session_id")
    private List<Long> sessionIds;

    private LocalDateTime timestamp;  // store booking creation time

    // Constructors
    public CricketBookingEntity() {}

    // Getters & Setters
    public Long getId() { return id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getChildId() { return childId; }
    public void setChildId(Long childId) { this.childId = childId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Boolean getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Boolean paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }
    public Double getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(Double discountApplied) { this.discountApplied = discountApplied; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public List<Long> getSessionIds() { return sessionIds; }
    public void setSessionIds(List<Long> sessionIds) { this.sessionIds = sessionIds; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
