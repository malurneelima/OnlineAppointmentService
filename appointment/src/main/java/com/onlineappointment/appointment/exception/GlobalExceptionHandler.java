package com.onlineappointment.appointment.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleGlobalExceptions(Exception exception,WebRequest webRequest)
	{
		ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),webRequest.getDescription(false),"INTERNAL_SERVER_ERROR",exception.getMessage());
		return new ResponseEntity<>(errorDetails,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(AppointmentException.class)
	public ResponseEntity<ErrorDetails> handleAppointmentException(AppointmentException exception,WebRequest webRequest)
	{
		ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),webRequest.getDescription(false),"RESOURCE_NOT_FOUND_ERROR",exception.getMessage());
		return new ResponseEntity<>(errorDetails,HttpStatus.BAD_REQUEST);
	}
	

	@ExceptionHandler(ServiceUnavailableException.class)
	public ResponseEntity<ErrorDetails> handleServiceUnavailableException(ServiceUnavailableException exception,WebRequest webRequest)
	{
		ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),webRequest.getDescription(false),"SERVICE_UNAVAILABLE_ERROR",exception.getMessage());
		return new ResponseEntity<>(errorDetails,HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,WebRequest webRequest)
	{
		ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),webRequest.getDescription(false),"RESOURCE_UNAVAILABLE_ERROR",exception.getMessage());
		return new ResponseEntity<>(errorDetails,HttpStatus.NOT_FOUND);
	}
}
