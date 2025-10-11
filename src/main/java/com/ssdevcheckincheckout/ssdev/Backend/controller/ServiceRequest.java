package com.ssdevcheckincheckout.ssdev.Backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssdevcheckincheckout.ssdev.Backend.dto.RegistrationServiceRequestDTO;
import com.ssdevcheckincheckout.ssdev.Backend.entity.RegistrationServiceRequest;
import com.ssdevcheckincheckout.ssdev.Backend.service.RegistrationRequestService;

@RestController
@RequestMapping("/api/auth/service-request")
public class ServiceRequest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRequest.class);

    @Autowired
    private RegistrationRequestService registrationRequestService;

    @PostMapping("/raise-register-request")
    public ResponseEntity<String> raiseRegistrationRequest(@RequestBody RegistrationServiceRequestDTO registrationServiceRequest) {
        // Log the incoming DTO
        logger.info("Received registration request DTO: {}", registrationServiceRequest);

        try {
            long serviceTicketId = registrationRequestService.raiseRegistrationServiceRequest(registrationServiceRequest).getId();
            logger.info("Successfully created service ticket with ID: {}", serviceTicketId);

            return ResponseEntity.status(HttpStatus.OK).body("Service Ticket Id is " + serviceTicketId);
        } catch (Exception e) {
            logger.error("Error while raising registration request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to raise service request");
        }
    }

    @GetMapping("/get-service-ticket-status/{id}")
    public ResponseEntity<RegistrationServiceRequest> getServiceTicketById(@PathVariable long id) {
        logger.info("Fetching service ticket status for ID: {}", id);
        RegistrationServiceRequest serviceRequest = registrationRequestService.findServiceRequestById(id);
        if (serviceRequest != null) {
            logger.info("Found service request: {}", serviceRequest);
            return ResponseEntity.ok(serviceRequest);
        } else {
            logger.warn("No service request found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    
    @GetMapping("/get-service-tickets/")
    public ResponseEntity<List<RegistrationServiceRequest>> getAllRegisterServiceTickets()
    {
    	logger.info("getting All RegisterTickers");
    	List<RegistrationServiceRequest> serviceRequests = registrationRequestService.getAllRequests();
    	
    	return ResponseEntity.ok(serviceRequests);
    	
    }
    
    @PostMapping("/approve-service-register-ticket/{id}")
    public ResponseEntity<String> approveRegistration(@PathVariable long id){
    	logger.info("Approving Registration Ticket");
    	
    	String ticketStatus =  registrationRequestService.approveRegisterRequest(id);
    	return ResponseEntity.ok(ticketStatus);
    }
}
