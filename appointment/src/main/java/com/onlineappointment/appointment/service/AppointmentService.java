package com.onlineappointment.appointment.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.onlineappointment.appointment.entity.Appointment;
import com.onlineappointment.appointment.entity.Availability;
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
    	 // Check doctor availability before saving the appointment
        boolean isAvailable = checkDoctorAvailability(appointment.getDoctor().getId(), appointment.getAppointmentDate());
        
        if (isAvailable) {
            // Save the appointment if doctor is available
            return appointmentRepository.save(appointment);
        } else {
            throw new RuntimeException("Doctor is not available at the requested time.");
        } 
    }
    //Fallback
    public Appointment createAppointmentFallback(Appointment appointment,Throwable throwable) {
   	 System.out.println("Fallback method triggered for createAppointment as :"+throwable.getMessage());
   	 throw new RuntimeException("Doctor service must be down");
       
   }
    
    private boolean checkDoctorAvailability(Long doctorId, LocalDateTime appointmentDate) {
        String urlData = url + doctorId ;
        
        // Making a GET request to Doctor Service to get availability
        List<LinkedHashMap<String, Object>> availableSlots= restTemplate.getForObject(urlData, List.class);
        
        if (availableSlots != null) { 
        	return availableSlots != null && availableSlots.stream()
                .map(slot -> {
                    LocalDateTime startTime = LocalDateTime.parse((String) slot.get("startTime"));
                    LocalDateTime endTime = LocalDateTime.parse((String) slot.get("endTime"));
                    Availability availability = new Availability();
                    availability.setStartTime(startTime);
                    availability.setEndTime(endTime);
                    return availability;
                })
                .anyMatch(app -> app.isAvailable(appointmentDate));
        	} 
        else {
            System.out.println("No available slots found.");
        }
     
		return false;
    }

    public Appointment updateAppointment(Long id, Appointment newAppointmentDetails) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if (appointmentOptional.isPresent()) {
            Appointment appointment = appointmentOptional.get();
            appointment.setAppointmentDate(newAppointmentDetails.getAppointmentDate());
            appointment.setStartTime(newAppointmentDetails.getStartTime());
            appointment.setEndTime(newAppointmentDetails.getEndTime());
            appointment.setDoctor(newAppointmentDetails.getDoctor());
            return appointmentRepository.save(appointment);
        }
        return null; // Handle error cases
    }

    public boolean cancelAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}

