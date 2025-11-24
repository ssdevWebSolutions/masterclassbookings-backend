package com.ssdevcheckincheckout.ssdev.Backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "get_in_touch")


public class GetInTouch {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String email;
	private String subject;
	private String message;
	
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GetInTouch(long id, String name, String email, String subject, String message) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.subject = subject;
		this.message = message;
	}

	public GetInTouch() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "GetInTouch [id=" + id + ", name=" + name + ", email=" + email + ", subject=" + subject + ", message="
				+ message + "]";
	}
	
	
	
	
	

}
