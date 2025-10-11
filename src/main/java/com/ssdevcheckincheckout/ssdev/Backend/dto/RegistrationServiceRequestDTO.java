package com.ssdevcheckincheckout.ssdev.Backend.dto;

public class RegistrationServiceRequestDTO {

	private String requesttype;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String message;
    private String timestamp;
    
	public RegistrationServiceRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegistrationServiceRequestDTO(String requestType, String firstName, String lastName, String email,
			String phoneNumber, String message, String timestamp) {
		super();
		this.requesttype = requestType;
		this.firstname = firstName;
		this.lastname = lastName;
		this.email = email;
		this.phonenumber = phoneNumber;
		this.message = message;
		this.timestamp = timestamp;
	}

	public String getRequesttype() {
		return requesttype;
	}

	public void setRequesttype(String requesttype) {
		this.requesttype = requesttype;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	
    
    
    
}
