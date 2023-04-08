package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.enums.Role;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.AppointmentMapper;
import com.example.dentalscheduler.model.Appointment;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.model.User;
import com.example.dentalscheduler.repository.AppointmentRepository;
import com.example.dentalscheduler.repository.DoctorRepository;
import com.example.dentalscheduler.repository.PatientRepository;
import com.example.dentalscheduler.security.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public List<AppointmentDTO> getAllAppointments() {

        List<Appointment> all = appointmentRepository.findAll();
        return appointmentMapper.toDTO(all);
    }

    public AppointmentDTO findById(Long id) {

        Optional<Appointment> byId = appointmentRepository.findById(id);
        return byId.map(appointmentMapper::toDTO).orElse(null);
    }

    public AppointmentDTO createAppointment(Long doctorId, String appointmentDate, String appointmentDetails) {

        Instant instant = Instant.parse(appointmentDate);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        LocalDateTime startDate = dateTime.minus(59, ChronoUnit.MINUTES);
        LocalDateTime endDate = dateTime.plus(59, ChronoUnit.MINUTES);

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new CustomException("You cannot create an appointment in the past", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        Long patientId = getPatientForAppointment(SecurityUtils.getCurrentUser());

        List<Appointment> doctorAppointments = appointmentRepository.findAllByAppointmentDateBetweenAndDoctorId(startDate, endDate, doctorId);
        if (!doctorAppointments.isEmpty()) {
            throw new CustomException("The doctor already has an appointment at this hour!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        List<Appointment> patientAppointments = appointmentRepository.findAllByAppointmentDateBetweenAndPatientId(startDate, endDate, patientId);
        if (!patientAppointments.isEmpty()) {
            throw new CustomException("You already have another appointment at this hour!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        AppointmentDTO appointment = new AppointmentDTO(appointmentDetails, doctorId, patientId, dateTime);
        Appointment appointmentEntity = appointmentMapper.toEntity(appointment);
        Appointment savedAppointment = appointmentRepository.save(appointmentEntity);
        return appointmentMapper.toDTO(savedAppointment);
    }

    private Long getPatientForAppointment(User user) {

        if (!Arrays.asList(Role.ADMIN, Role.CLIENT).contains(user.getRole())) {
            throw new CustomException("You are not allowed to do this action!", HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value());
        }

        Optional<Patient> byUserId = patientRepository.findByUserId(user.getId());
        if (byUserId.isEmpty()) {
            throw new CustomException("Patient does not exists!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }
        return byUserId.get().getId();
    }

    private Long getDoctorForAppointment(User user) {

        if (!Arrays.asList(Role.ADMIN, Role.EMPLOYEE).contains(user.getRole())) {
            throw new CustomException("You are not allowed to do this action!", HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value());
        }

        Optional<Doctor> byUserId = doctorRepository.findByUserId(user.getId());
        if (byUserId.isEmpty()) {
            throw new CustomException("Doctor does not exists!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }
        return byUserId.get().getId();
    }

    public AppointmentDTO editAppointment(AppointmentDTO appointmentDTO) {

        Optional<Appointment> byId = appointmentRepository.findById(appointmentDTO.id());
        if (byId.isEmpty()) {
            throw new CustomException("Appointment does not exists!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        Long userId = SecurityUtils.getUserId();

        patientRepository.findByUserId(userId).ifPresent(patient -> {
            if (!byId.get().getPatient().getId().equals(patient.getId())) {
                throw new CustomException("Appointment is not included in your appointment list!", HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value());
            }
        });

        Appointment appointment = byId.get();
        appointment.setAppointmentDetails(appointmentDTO.appointmentDetails());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toDTO(updatedAppointment);
    }

    public List<AppointmentDTO> getAllAppointmentsForPatient(Long patientId) {

        List<Appointment> allByPatientId = appointmentRepository.findAllByPatientId(patientId);
        return appointmentMapper.toDTO(allByPatientId);
    }

    public List<AppointmentDTO> getAllAppointmentsForDoctor(Long doctorId) {

        List<Appointment> allByDoctorId = appointmentRepository.findAllByDoctorId(doctorId, Sort.by(Sort.Direction.ASC, "appointmentDate"));
        return appointmentMapper.toDTO(allByDoctorId);
    }

    @Transactional
    public AppointmentDTO createAppointmentAsDoctor(String appointmentDate, String appointmentDetails) {

        Instant instant = Instant.parse(appointmentDate);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        LocalDateTime startDate = dateTime.minus(59, ChronoUnit.MINUTES);
        LocalDateTime endDate = dateTime.plus(59, ChronoUnit.MINUTES);

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new CustomException("You cannot create an appointment in the past!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        Long doctorId = getDoctorForAppointment(SecurityUtils.getCurrentUser());

        List<Appointment> doctorAppointments = appointmentRepository.findAllByAppointmentDateBetweenAndDoctorId(startDate, endDate, doctorId);
        if (!doctorAppointments.isEmpty()) {
            throw new CustomException("You cannot reserve this slot!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        AppointmentDTO appointment = new AppointmentDTO(appointmentDetails, doctorId, null, dateTime);
        Appointment appointmentEntity = appointmentMapper.toEntity(appointment);
        Appointment savedAppointment = appointmentRepository.save(appointmentEntity);
        return appointmentMapper.toDTO(savedAppointment);
    }

    public void deleteAppointment(Long appointmentId) {

        Optional<Appointment> byId = appointmentRepository.findById(appointmentId);
        if (byId.isEmpty()) {
            throw new CustomException("Appointment does not exist!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        appointmentRepository.deleteById(appointmentId);
    }
}
