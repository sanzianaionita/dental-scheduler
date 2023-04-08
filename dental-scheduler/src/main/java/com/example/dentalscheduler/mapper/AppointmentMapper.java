package com.example.dentalscheduler.mapper;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.mapper.resolvers.DoctorMapperResolver;
import com.example.dentalscheduler.mapper.resolvers.PatientMapperResolver;
import com.example.dentalscheduler.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PatientMapperResolver.class, DoctorMapperResolver.class})
public interface AppointmentMapper {


    @Mapping(target = "doctor", source = "doctorId")
    @Mapping(target = "patient", source = "patientId")
    Appointment toEntity(AppointmentDTO appointmentDTO);

    @Mapping(target = "doctorName", expression = "java(appointment.getDoctor() == null ?  null : appointment.getDoctor().getFirstName() + \" \" + appointment.getDoctor().getLastName())")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientName", expression = "java(appointment.getPatient() == null ? null : appointment.getPatient().getFirstName() + \" \" + appointment.getPatient().getLastName())")
    @Mapping(target = "patientId", source = "patient.id")
    AppointmentDTO toDTO(Appointment appointment);

    List<Appointment> toEntity(List<AppointmentDTO> appointmentDTOs);

    List<AppointmentDTO> toDTO(List<Appointment> appointments);
}
