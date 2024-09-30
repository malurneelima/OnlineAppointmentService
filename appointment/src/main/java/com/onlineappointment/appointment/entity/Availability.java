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



	public boolean isAvailable(LocalDateTime appointmentDate) {
        return !appointmentDate.isBefore(startTime) && !appointmentDate.isAfter(endTime);
    }
}
