package com.maiia.pro.controller;

import com.maiia.pro.dto.AppointmentDTO;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.service.ProAppointmentService;
import com.maiia.pro.service.ProAvailabilityService;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProAppointmentController {
	
    @Autowired
    private ProAppointmentService proAppointmentService;

    @Autowired
    private ProAvailabilityService proAvailabilityService;

    @ApiOperation(value = "Get appointments by practitionerId")
    @GetMapping("/{practitionerId}")
    public List<Appointment> getAppointmentsByPractitioner(@PathVariable final Integer practitionerId) {
        return proAppointmentService.findByPractitionerId(practitionerId);
    }

    @ApiOperation(value = "Get all appointments")
    @GetMapping
    public List<Appointment> getAppointments() {
        return proAppointmentService.findAll();
    }
    
    @PostMapping(value ="/appointment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> addAppointment(@RequestBody(required = true) AppointmentDTO dto) {
    	if (proAvailabilityService.checkPractitionnerAvailability(dto.getPractitionerId(), dto.getStartDate(), dto.getEndDate())) {
    		proAvailabilityService.removeObsoleteAvailabilities(dto.getPractitionerId(), dto.getStartDate(), dto.getEndDate());
    		proAppointmentService.save(dto.getPatientId(), dto.getPractitionerId(), dto.getStartDate(), dto.getEndDate());
    		return ResponseEntity.ok().body(dto);
    	}
    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Créneau horaire indisponible");
    }
}
