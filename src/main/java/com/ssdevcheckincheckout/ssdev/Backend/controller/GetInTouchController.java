package com.ssdevcheckincheckout.ssdev.Backend.controller;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssdevcheckincheckout.ssdev.Backend.entity.GetInTouch;
import com.ssdevcheckincheckout.ssdev.Backend.service.GetInTouchService;


@RestController
@RequestMapping("/api/auth")
public class GetInTouchController {
	
	@Autowired
	private GetInTouchService getInTouchService;
	
	 
    @PostMapping("/getIntouch/add")
    public ResponseEntity<GetInTouch> addDetails(@RequestBody GetInTouch getInTouch) {
    	GetInTouch saveDetails = getInTouchService.addDetails(getInTouch);
//        return ResponseEntity.ok(saveDetails);
    	return ResponseEntity.status(HttpStatus.SC_OK).body(saveDetails);
    }
    
    @GetMapping("/getIntouch/getAll")
    public ResponseEntity<List<GetInTouch>> getDetails(){
    	
    	return ResponseEntity.ok(getInTouchService.getDetails());
    	
    }
    
    @GetMapping("/getIntouch/getById/{id}")
    public ResponseEntity<GetInTouch> getById(@PathVariable Long id){
    	
    	return ResponseEntity.ok(getInTouchService.getById(id));
    	
    }
//    @PutMapping("/getInTouch/updateById/{{id}")
//    public ResponseEntity<GetInTouch> updateById( String email,String message){
//    	 return ResponseEntity.ok (getInTouchService.updateMessageByEmail(email, message));
//    	
//    }
    
    @DeleteMapping("/getIntouch/deleteById/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
    	getInTouchService.deleteById(id);
    	 return ResponseEntity.noContent().build();
    }

}
