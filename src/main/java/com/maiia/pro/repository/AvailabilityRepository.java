package com.maiia.pro.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.maiia.pro.entity.Availability;

@Repository
public interface AvailabilityRepository extends CrudRepository<Availability, String> {
    List<Availability> findByPractitionerId(Integer practitionerId);

    List<Availability> findAll();
    
    @Query("from Availability av where av.practitionerId = ?1 and (av.patientId is null or av.patientID = ?2) and av.startDate >= ?3 and av.endDate <= ?4 order by av.startDate")
    List<Availability> findByCriteria(Integer practitionerId, Integer patientId, LocalDateTime startDate, LocalDateTime endDateTime);

}
