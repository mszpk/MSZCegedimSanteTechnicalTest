package com.maiia.pro.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.maiia.pro.entity.Availability;

@Repository
public interface AvailabilityRepository extends CrudRepository<Availability, String> {
    List<Availability> findByPractitionerId(Integer practitionerId);

    @Query("from Availability av where av.practitionerId = ?1 and av.startDate >= ?2 and av.endDate <= ?3 order by av.startDate")
    List<Availability> findByCriteria(Integer practitionerId, LocalDateTime startDate, LocalDateTime endDateTime);

    @Modifying
    @Query("delete from Availability av where av.practitionerId = ?1 and av.startDate >= ?2 and av.endDate <= ?3")
    void deleteByCriteria(Integer practitionerId, LocalDateTime startDate, LocalDateTime endDateTime);
}
