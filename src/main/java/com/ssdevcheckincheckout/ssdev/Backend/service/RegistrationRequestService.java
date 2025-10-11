package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.dto.RegistrationServiceRequestDTO;
import com.ssdevcheckincheckout.ssdev.Backend.entity.RegistrationServiceRequest;
import com.ssdevcheckincheckout.ssdev.Backend.entity.Role;
import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import com.ssdevcheckincheckout.ssdev.Backend.repository.RegistrationService;
import com.ssdevcheckincheckout.ssdev.Backend.repository.UserRepository;

@Service
public class RegistrationRequestService {
	
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private SendGridEmailService emailService;
	
	@Autowired
	private  PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	
//	API TO RAISE A REGISTRATION REQUEST WITH STATUS PENING
	public RegistrationServiceRequest raiseRegistrationServiceRequest(RegistrationServiceRequestDTO registrationServiceRequest)
	{
		RegistrationServiceRequest registerRequest = new  RegistrationServiceRequest();
		
		registerRequest.setFirstName(registrationServiceRequest.getFirstname());
		registerRequest.setLastName(registrationServiceRequest.getLastname());
		registerRequest.setEmail(registrationServiceRequest.getEmail());
		registerRequest.setPhoneNumber(registrationServiceRequest.getPhonenumber());
		registerRequest.setMessage(registrationServiceRequest.getMessage());
		registerRequest.setRequestType(registrationServiceRequest.getRequesttype());
		registerRequest.setStatus("PENDING");
		registerRequest.setTimestamp(registrationServiceRequest.getTimestamp());
		
		RegistrationServiceRequest serviceTicket = registrationService.save(registerRequest);
		
		// Send confirmation email
	    try {
	        emailService.sendServiceRequestConfirmation(
	            serviceTicket.getEmail(),
	            serviceTicket.getFirstName() + " " + serviceTicket.getLastName(),
	            serviceTicket.getId(),
	            serviceTicket.getRequestType(),
	            serviceTicket.getMessage()
	        );
	    } catch (IOException e) {
	        System.err.println("Error sending service request email: " + e.getMessage());
	    }
		
		
		return serviceTicket;
	}
	
	
//  API TO GET LIST OF ALL SERVICE REQUESTS TO ADMIN SIDE
	public List<RegistrationServiceRequest> getAllRequests()
	{
		return registrationService.findAll();
	}
	

//	API TO GET SERVICE REQUEST STATUS FOR USER SIDE
	public RegistrationServiceRequest findServiceRequestById(long id)
	{
		Optional<RegistrationServiceRequest> serviceRequest = registrationService.findById(id);
		
		if(serviceRequest.isPresent())
		{
			return serviceRequest.get();
		}
		
		return null;
 	}
	
	
	
	public String approveRegisterRequest(long id) {
		
		Optional<RegistrationServiceRequest> registerRequest = registrationService.findById(id);
		
		
		if(registerRequest.isPresent()) {
			
			 Role userRole =  Role.USER;
			 
			User user = new User.Builder()
	                .setEmail(registerRequest.get().getEmail())
	                .setPassword(passwordEncoder.encode("Master@1234"))
	                .setFirstName(registerRequest.get().getFirstName())
	                .setLastName(registerRequest.get().getLastName())
	                .setPhoneNumber(registerRequest.get().getPhoneNumber())
	                .setRole(userRole)
	                .setEnabled(true)
	                .build();
			
			User newUser =  userRepository.save(user);
			if(newUser !=null)
			{
				registerRequest.get().setStatus("APPROVED");
				registrationService.save(registerRequest.get());
				
				try {
					emailService.sendRegistrationApprovalEmail(
			            newUser.getEmail(),
			            newUser.getFirstName() + " " + newUser.getLastName(),
			            newUser.getId(),
			            "Master@1234" // default password
			        );
			    } catch (IOException e) {
//			        log.error("Failed to send registration approval email: {}", e.getMessage(), e);
			    }
			}
			 
			return "Approved"+newUser.getId() + "service Ticket id "+ id;
		}
		
		return null;
		
	}
	

}
