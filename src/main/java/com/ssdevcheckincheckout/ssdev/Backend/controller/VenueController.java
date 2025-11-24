package com.ssdevcheckincheckout.ssdev.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssdevcheckincheckout.ssdev.Backend.entity.Venues;
import com.ssdevcheckincheckout.ssdev.Backend.service.VenueService;

@RestController
@RequestMapping("api/auth")
//@CrossOrigin(origins = "*")
public class VenueController {
    
    @Autowired
    private VenueService venueService;
    
    @PostMapping("/venue/addvenue")
    public ResponseEntity<Venues> addVenueDetails(@RequestBody Venues venueDetails) {
        System.out.println("Received Venue Name: " + venueDetails.getVenueName());
        System.out.println("Received Address Line 1: " + venueDetails.getAddressLine1());

        Venues savedVenue = venueService.addVenueDetails(venueDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVenue);
    }
    
    @GetMapping("/venue/getAllVenueDetaails")
    public List<Venues> getAllVenueDetails()
    {
    	 return venueService.getAllVenues();   
    }
    
    
    @GetMapping("/venue/getById/{id}")
    public Venues getVenueByID(@PathVariable Long id)
    {
    	return venueService.getVenueById(id);
    }
    
//    put/update works only when all value from frontend recived 
    @PutMapping("/venue/update/{id}")
    public Venues updateVenue(@PathVariable Long id, @RequestBody Venues venue) {
        return venueService.updateVenue(id, venue);
    }
    
    @DeleteMapping("/venue/getById/{id}")
    public String deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return "Venue deleted successfully!";
    }
    
    
}
