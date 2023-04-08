package com.example.dentalscheduler.mapper;

import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AppointmentMapper.class})
public interface DoctorMapper {

    Doctor toEntity(DoctorDTO doctorDTO);

    DoctorDTO toDTO(Doctor doctor);

    List<Doctor> toEntity(List<DoctorDTO> doctorDTOs);

    List<DoctorDTO> toDTO(List<Doctor> doctors);
}
