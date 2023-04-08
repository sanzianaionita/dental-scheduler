package com.example.dentalscheduler.mapper;

import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.model.Patient;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AppointmentMapper.class})
public interface PatientMapper {

    Patient toEntity(PatientDTO patientDTO);

    PatientDTO toDTO(Patient patient);

    List<Patient> toEntity(List<PatientDTO> patientDTOs);

    List<PatientDTO> toDTO(List<Patient> patients);
}
