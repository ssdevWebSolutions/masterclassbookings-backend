package com.ssdevcheckincheckout.ssdev.Backend.dto;

import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    @Min(50) // Minimum amount in cents
    private Long amount;
    
    @NotBlank
    private String currency;
    
    private String description;
    private Map<String, String> metadata;
	public PaymentRequest(@NotNull @Min(50) Long amount, @NotBlank String currency, String description,
			Map<String, String> metadata) {
		super();
		this.amount = amount;
		this.currency = currency;
		this.description = description;
		this.metadata = metadata;
	}
	public PaymentRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, String> getMetadata() {
		return metadata;
	}
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
    
    
    
    
    
    // getters and setters
}