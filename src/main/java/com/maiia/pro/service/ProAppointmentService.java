package com.maiia.pro.service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProAppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment find(String appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow();
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> findByPractitionerId(Integer practitionerId) {
        return appointmentRepository.findByPractitionerId(practitionerId);
    }
    
    public Appointment save(Integer patientId, Integer practitionerId, LocalDateTime startDate, LocalDateTime endDate) {
    	Appointment app = new Appointment();
    	app.setPatientId(patientId);
    	app.setPractitionerId(practitionerId);
    	app.setStartDate(startDate);
    	app.setEndDAte(endDate);
    	return appointmentRepository.save(app);
    }
}
