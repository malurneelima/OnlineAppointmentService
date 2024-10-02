package com.onlineappointment.appointment.entity;

import java.time.LocalDateTime;

public class Availability {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

   

    public LocalDateTime getStartTime() {
		return startTime;
	}



	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}



	public LocalDateTime getEndTime() {
		return endTime;
	}



	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}



	public boolean isAvailable(LocalDateTime appointmentDate, LocalDateTime startTime, LocalDateTime endTime) {
	    // Check if appointment is on the same day as the start and end times
	    if (!appointmentDate.toLocalDate().equals(startTime.toLocalDate())) {
	        return false; // If the appointment date doesn't match the availability date, return false
	    }

	    // Check if the appointment time is within the start and end times on the same day
	    return (appointmentDate.toLocalTime().equals(startTime.toLocalTime()) || appointmentDate.toLocalTime().isAfter(startTime.toLocalTime())) 
	            && (appointmentDate.toLocalTime().equals(endTime.toLocalTime()) || appointmentDate.toLocalTime().isBefore(endTime.toLocalTime()));
	}

}
