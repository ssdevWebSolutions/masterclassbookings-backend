package com.ssdevcheckincheckout.ssdev.Backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;

public class GetInTouchException  extends RuntimeException{
	
	public GetInTouchException(Long id) {
		
		super("GetInTouch record not found with ID: " + id);
    }
	}



