package com.maiia.pro.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    
    public List<Availability> findAll() {
        return availabilityRepository.findAll();
    }

    public List<Availability> generateAvailabilities(Integer practitionerId, List<TimeSlot> slots) {

    	Map<LocalDate, Set<Availability>> map = new HashMap<LocalDate, Set<Availability>>();

    	final Duration criteria = Duration.ofMinutes(15);
    	LocalDate currentDay = slots.get(0).getStartDate().toLocalDate();
    	map.put(currentDay, new HashSet<>());
    	for (TimeSlot slot : slots) {
    		final LocalTime endTime = slot.getEndDate().toLocalTime();
    		LocalDateTime currentStartDateTime = slot.getStartDate();
    		Set<Availability> availabilities = map.get(currentDay);
    		if (availabilities == null) {
    			availabilities = new HashSet<Availability>();
    		}
    		while (Duration.between(slot.getStartDate().toLocalTime(), endTime).compareTo(criteria) != 0) {
    			// si plus de 15 min => découper en créneaux horaires de 15 minutes
    			LocalDateTime currentEndDateTime = currentStartDateTime.plus(criteria);
    			availabilities.add(new Availability(-1, practitionerId, currentStartDateTime, currentEndDateTime));
    			currentStartDateTime = currentEndDateTime;
    		}
    		map.put(currentDay, availabilities);
    	}

    	List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);
    	for (Appointment app : appointments) {
    		LocalTime appStartTime = app.getStartDate().toLocalTime();
			LocalTime appEndTime = app.getEndDate().toLocalTime();
			
			map.get(app.getStartDate().toLocalDate()).removeIf(av -> appStartTime.equals(av.getStartDate().toLocalTime()) 
					&& (appEndTime.equals(av.getEndDate().toLocalTime()) || appEndTime.isAfter(av.getEndDate().toLocalTime())));
    	}
    	
    	List<Availability> result = new ArrayList<Availability>();
    	map.values().stream().forEach(set -> result.addAll(set));
    	
    	availabilityRepository.saveAll(result);
        return result;
    }
    
    public boolean checkPractitionnerAvailability(Integer practitionerId, Integer patientId, LocalDateTime startDate, LocalDateTime endDate) {
    	List<Availability> avs = availabilityRepository.findByCriteria(practitionerId, patientId, startDate, endDate);
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
}
