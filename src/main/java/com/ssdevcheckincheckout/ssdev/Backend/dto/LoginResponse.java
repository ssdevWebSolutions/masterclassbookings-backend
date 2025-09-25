package com.ssdevcheckincheckout.ssdev.Backend.dto;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Role;

public class LoginResponse {
	
	private String fullName;
	
	private String mobileNumber;
	
	private Role role;
	
	private String email;
	
	private String token;
	
	private long id;

	public LoginResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginResponse(String fullName, String mobileNumber, Role role, String email, String token, long id) {
		super();
		this.fullName = fullName;
		this.mobileNumber = mobileNumber;
		this.role = role;
		this.email = email;
		this.token = token;
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	
	
	

}
