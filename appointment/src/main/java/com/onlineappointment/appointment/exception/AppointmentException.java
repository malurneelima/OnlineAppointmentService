package com.onlineappointment.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AppointmentException extends RuntimeException{
	
	private String resourceName;
	
	public AppointmentException(String resourceName)
	{
		super(String.format("%s is not available '",resourceName));
		this.resourceName=resourceName;
	}
	

}
