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
    private List<String> sessionDetails;
    private String phoneNumber;
    private int age;
    private String club;
    private String medicalInfo;
    private String level;
	
    
    public BookingResponseDto() {
		super();
	}


	public BookingResponseDto(Long bookingId, String parentName, String parentEmail, String kidName, String kidLevel,
			Double totalAmount, Boolean paymentStatus, List<String> sessionDetails, String phoneNumber, int age,
			String club, String medicalInfo, String level) {
		super();
		this.bookingId = bookingId;
		this.parentName = parentName;
		this.parentEmail = parentEmail;
		this.kidName = kidName;
		this.kidLevel = kidLevel;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
		this.sessionDetails = sessionDetails;
		this.phoneNumber = phoneNumber;
		this.age = age;
		this.club = club;
		this.medicalInfo = medicalInfo;
		this.level = level;
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


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public String getClub() {
		return club;
	}


	public void setClub(String club) {
		this.club = club;
	}


	public String getMedicalInfo() {
		return medicalInfo;
	}


	public void setMedicalInfo(String medicalInfo) {
		this.medicalInfo = medicalInfo;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	@Override
	public String toString() {
		return "BookingResponseDto [bookingId=" + bookingId + ", parentName=" + parentName + ", parentEmail="
				+ parentEmail + ", kidName=" + kidName + ", kidLevel=" + kidLevel + ", totalAmount=" + totalAmount
				+ ", paymentStatus=" + paymentStatus + ", sessionDetails=" + sessionDetails + ", phoneNumber="
				+ phoneNumber + ", age=" + age + ", club=" + club + ", medicalInfo=" + medicalInfo + ", level=" + level
				+ "]";
	}
    
	

    
}
