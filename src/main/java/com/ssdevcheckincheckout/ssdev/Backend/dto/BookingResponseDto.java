package com.ssdevcheckincheckout.ssdev.Backend.dto;


import java.util.List;

public class BookingResponseDto {
    private Long bookingId;
    private String parentName;
    private String parentEmail;
    private String kidName;
    private String kidLevel;
    private Double totalAmount;
    private Boolean paymentStatus;
    private List<String> sessionDetails; // e.g. "Sunday - Class 1 at 10AM"
	public BookingResponseDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BookingResponseDto(Long bookingId, String parentName, String parentEmail, String kidName, String kidLevel,
			Double totalAmount, Boolean paymentStatus, List<String> sessionDetails) {
		super();
		this.bookingId = bookingId;
		this.parentName = parentName;
		this.parentEmail = parentEmail;
		this.kidName = kidName;
		this.kidLevel = kidLevel;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
		this.sessionDetails = sessionDetails;
	}
	public Long getBookingId() {
		return bookingId;
	}
	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getParentEmail() {
		return parentEmail;
	}
	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}
	public String getKidName() {
		return kidName;
	}
	public void setKidName(String kidName) {
		this.kidName = kidName;
	}
	public String getKidLevel() {
		return kidLevel;
	}
	public void setKidLevel(String kidLevel) {
		this.kidLevel = kidLevel;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Boolean getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public List<String> getSessionDetails() {
		return sessionDetails;
	}
	public void setSessionDetails(List<String> sessionDetails) {
		this.sessionDetails = sessionDetails;
	}

    // getters & setters
}
