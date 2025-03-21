package com.maiia.pro.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;

@Service
public class ProAvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Availability> findByPractitionerId(Integer practitionerId) {
        return availabilityRepository.findByPractitionerId(practitionerId);
    }
    
    public Iterable<Availability> findAll() {
        return availabilityRepository.findAll();
    }

    public void generateAvailabilities(Integer practitionerId, List<TimeSlot> slots) {

    	final Duration criteria = Duration.ofMinutes(15);
    	for (TimeSlot slot : slots) {
    		Set<Availability> availabilities = new HashSet<Availability>();
    		final LocalTime endTime = slot.getEndDate().toLocalTime();
    		LocalDateTime currentStartDateTime = slot.getStartDate();
    		while (Duration.between(slot.getStartDate().toLocalTime(), endTime).compareTo(criteria) != 0) {
    			// si plus de 15 min => découper en créneaux horaires de 15 minutes
    			LocalDateTime currentEndDateTime = currentStartDateTime.plus(criteria);
    			Availability av = new Availability();
    			av.setPractitionerId(practitionerId);
    			av.setStartDate(currentStartDateTime);
    			av.setEndDate(currentEndDateTime);
    			availabilities.add(av);
    			currentStartDateTime = currentEndDateTime;
    		}
    		availabilityRepository.saveAll(availabilities);
    	}

    	List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);
    	for (Appointment app : appointments) {
			removeObsoleteAvailabilities(practitionerId, app.getStartDate(), app.getEndDate());
    	}
    	
    }
    
    public boolean checkPractitionnerAvailability(Integer practitionerId, LocalDateTime startDate, LocalDateTime endDate) {
    	List<Availability> avs = availabilityRepository.findByCriteria(practitionerId, startDate, endDate);
    	LocalDateTime currentStartDate = startDate;
    	for (int cpt = 0; cpt < avs.size(); cpt++) {
    		if (!avs.get(cpt).getStartDate().equals(currentStartDate)) {
    			return false;
    		}
    		currentStartDate = avs.get(cpt).getEndDate();
    	}
    	if (!avs.get(avs.size() - 1).getEndDate().equals(endDate)) {
    		return false;
    	}
    	return true;
    }
    
    public void removeObsoleteAvailabilities(Integer practitionerId, LocalDateTime startDate, LocalDateTime endDateTime) {
    	availabilityRepository.deleteByCriteria(practitionerId, startDate, endDateTime);
    }
}
