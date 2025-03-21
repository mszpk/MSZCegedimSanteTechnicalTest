package com.maiia.pro.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.maiia.pro.entity.Appointment;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, String> {
    List<Appointment> findByPractitionerId(Integer practitionerId);
    List<Appointment> findAll();
    @SuppressWarnings("unchecked")
	Appointment save(Appointment appointment);
    
}
