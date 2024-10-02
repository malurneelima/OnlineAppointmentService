package com.onlineappointment.appointment.exception;

import java.time.LocalDateTime;

public class ErrorDetails {

	private LocalDateTime timeStamp;
	private String path;
	private String errorCode;
	private String errorMessage;
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public ErrorDetails(LocalDateTime timeStamp, String path, String errorCode, String errorMessage) {
		super();
		this.timeStamp = timeStamp;
		this.path = path;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	public ErrorDetails() {
		super();
	}
	
	
}
