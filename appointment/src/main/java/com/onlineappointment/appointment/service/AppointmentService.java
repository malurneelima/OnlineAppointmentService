package com.onlineappointment.appointment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.onlineappointment.appointment.entity.Appointment;
import com.onlineappointment.appointment.entity.Availability;
import com.onlineappointment.appointment.exception.AppointmentException;
import com.onlineappointment.appointment.exception.ResourceNotFoundException;
import com.onlineappointment.appointment.exception.ServiceUnavailableException;
import com.onlineappointment.appointment.repository.AppointmentRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Value("${doctoravailability.service.url}")
    private String url;
    
    private final RestTemplate restTemplate;
    
    @Autowired 
    public AppointmentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @CircuitBreaker(name="createAppointmentExt" , fallbackMethod = "createAppointmentFallback")
    public Appointment createAppointment(Appointment appointment) {
        boolean data=checkDoctorAvailability(appointment.getDoctor().getId(), appointment.getAppointmentDate());
        if(data) {
        	return appointmentRepository.save(appointment);
        }
        return null;
      
     
    }

    public Appointment createAppointmentFallback(Appointment appointment,Throwable throwable) {
   	 System.out.println("Fallback method triggered for createAppointment as :"+throwable.getMessage());
   	 throw new ServiceUnavailableException("Doctor service is down, please try again later.");
       
   }
    
    private boolean checkDoctorAvailability(Long doctorId, LocalDateTime appointmentDate) {
        String urlData = url + doctorId ;
        List<LinkedHashMap<String, Object>> availableSlots;
        try {
            availableSlots = restTemplate.getForObject(urlData, List.class);
        } catch (Exception e) {
            System.out.println("Error fetching availability: " + e.getMessage());
            throw new ServiceUnavailableException("Doctor service is temporarily unavailable. Please try again later.");
        }

        if (availableSlots == null || availableSlots.isEmpty()) {
            return false;
        }

        for (LinkedHashMap<String, Object> slot : availableSlots) {
            String startTimeString = (String) slot.get("startTime");
            String endTimeString = (String) slot.get("endTime");

            LocalDateTime startTime = LocalDateTime.parse(startTimeString);
            LocalDateTime endTime = LocalDateTime.parse(endTimeString);

            Availability availability = new Availability();
            availability.setStartTime(startTime);
            availability.setEndTime(endTime);

            // Check if the appointment date falls within the available time slot
            if (availability.isAvailable(appointmentDate, startTime, endTime)) {
                return true; // Doctor is available, continue with the process
            }
        }
		return false;

      
    }

    @CircuitBreaker(name="updateAppointmentdb", fallbackMethod = "updateAppointmentFallback")
    public Appointment updateAppointment(Long id, Appointment newAppointmentDetails) {
        Appointment appointmentOptional = appointmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("updateAppointment", "user", id));
            appointmentOptional.setAppointmentDate(newAppointmentDetails.getAppointmentDate());
            appointmentOptional.setStartTime(newAppointmentDetails.getStartTime());
            appointmentOptional.setEndTime(newAppointmentDetails.getEndTime());
            appointmentOptional.setDoctor(newAppointmentDetails.getDoctor());
            return appointmentRepository.save(appointmentOptional);
    }
    public Appointment updateAppointmentFallback(Long id, Appointment newAppointmentDetails,Throwable throwable) {
        System.out.println("Fallback triggered by updateAppointment due to "+throwable.getMessage());
        throw new AppointmentException("User id for update Appointment"); // Handle error cases
    }

   // @CircuitBreaker(name="cancelAppointmentdb" , fallbackMethod = "cancelAppointmentFallback")
    public boolean cancelAppointment(Long id) {
    	appointmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Cancel Appointment", "user id", id));
    	 
            appointmentRepository.deleteById(id);
            return true;
    }
    public boolean cancelAppointmentFallback(Long id,Throwable throwable) {
    	System.out.println("Fallback method triggered for getAllAppointments as :"+throwable.getMessage());
    	return false;
    }

    @CircuitBreaker(name="getAllAppointmentsdb", fallbackMethod = "getAllAppointmentsFallback")
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public List<Appointment> getAllAppointmentsFallback(Throwable throwable) {
    	System.out.println("Fallback method triggered for getAllAppointments as :"+throwable.getMessage());
        return new ArrayList<>();
    }
}

