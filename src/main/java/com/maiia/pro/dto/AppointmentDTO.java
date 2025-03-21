package com.maiia.pro.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("serial")
@AllArgsConstructor
@Data
public class AppointmentDTO implements Serializable {
	  private Integer patientId;
	  private Integer practitionerId;
	  private LocalDateTime startDate;
	  private LocalDateTime endDate;
}
