package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.AppointmentMapperImpl;
import com.example.dentalscheduler.model.Appointment;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.repository.AppointmentRepository;
import com.example.dentalscheduler.repository.DoctorRepository;
import com.example.dentalscheduler.repository.PatientRepository;
import com.example.dentalscheduler.security.util.SecurityUtils;
import com.example.dentalscheduler.utils.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {

    private static AppointmentRepository appointmentRepository;
    private static AppointmentService appointmentService;
    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    private static AppointmentMapperImpl appointmentMapper;
    private static PatientRepository patientRepository;
    private static DoctorRepository doctorRepository;

    @BeforeAll
    public static void setup() {

        appointmentRepository = mock(AppointmentRepository.class);
        appointmentMapper = mock(AppointmentMapperImpl.class);
        patientRepository = mock(PatientRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        appointmentService = new AppointmentService(appointmentMapper, appointmentRepository, patientRepository, doctorRepository);
    }

    @AfterAll
    public static void close() {
        securityUtilsMockedStatic.close();
    }

    @Test
    public void testGetAllAppointments() {

        when(appointmentRepository.findAll()).thenReturn(Collections.singletonList(Utils.returnAppointment()));
        when(appointmentMapper.toDTO(anyList())).thenReturn(Collections.singletonList(Utils.returnAppointmentDto()));

        List<AppointmentDTO> allAppointments = appointmentService.getAllAppointments();
        assertNotEquals(0, allAppointments.size());
    }

    @Test
    public void testFindById_expectReturnObject() {

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(Utils.returnAppointment()));
        when(appointmentMapper.toDTO(any(Appointment.class))).thenReturn(Utils.returnAppointmentDto());

        AppointmentDTO byId = appointmentService.findById(any());
        assertNotNull(byId);
    }

    @Test
    public void testFindById_expectReturnNull() {

        when(appointmentRepository.findById(any())).thenReturn(Optional.empty());

        AppointmentDTO byId = appointmentService.findById(any());
        assertNull(byId);
    }

    @Test
    public void testCreateAppointment_expectDateOverlaps() {

        when(appointmentRepository.findAllByAppointmentDateBetweenAndDoctorId(any(), any(), any())).thenReturn(Collections.singletonList(Utils.returnAppointment()));
        securityUtilsMockedStatic.when(SecurityUtils::getCurrentUser).thenReturn(Utils.createClientUser());
        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(Utils.createPatient()));

        CustomException exception = assertThrows(CustomException.class, () -> {
            appointmentService.createAppointment(0L, Utils.formatDate(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)), "details");
        });

        String expectedMessage = "The doctor already has an appointment at this hour!";
        String actualMessage = exception.getErrorMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCreateAppointment_success() {

        when(appointmentRepository.findAllByAppointmentDateBetweenAndDoctorId(any(), any(), any())).thenReturn(Collections.emptyList());
        when(appointmentMapper.toDTO(any(Appointment.class))).thenReturn(Utils.returnAppointmentDto());

        Appointment appointment = Utils.returnAppointment();
        Instant instant = Instant.parse("2023-04-04T18:10:00Z");
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        appointment.setAppointmentDate(dateTime);

        securityUtilsMockedStatic.when(SecurityUtils::getCurrentUser).thenReturn(Utils.createClientUser());
        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(Utils.createPatient()));

        when(appointmentRepository.findAllByPatientId(any())).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any())).thenReturn(Utils.returnAppointment());

        AppointmentDTO appointmentDTO = appointmentService.createAppointment(1L, Utils.formatDate(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)), "details");
        assertNotNull(appointmentDTO);
    }

    @Test
    public void testEditAppointment() {

        Patient patient = Utils.createPatient();
        Appointment appointment = Utils.returnAppointment();
        AppointmentDTO appointmentDTO = Utils.returnAppointmentDto();

        appointment.setPatient(patient);

        when(appointmentRepository.findById(appointmentDTO.id())).thenReturn(Optional.of(appointment));
        when(patientRepository.findByUserId(appointmentDTO.patientId())).thenReturn(Optional.of(patient));
        when(SecurityUtils.getUserId()).thenReturn(appointmentDTO.patientId());

        when(appointmentMapper.toEntity(any(AppointmentDTO.class))).thenReturn(appointment);
        when(appointmentRepository.save(any())).thenReturn(appointment);

        when(appointmentMapper.toDTO(any(Appointment.class))).thenReturn(appointmentDTO);

        AppointmentDTO appointmentDTO1 = appointmentService.editAppointment(appointmentDTO);
        assertNotNull(appointmentDTO1);
    }

    @Test
    public void testGetAllAppointmentsForPatient() {

        when(appointmentRepository.findAllByPatientId(any())).thenReturn(Collections.singletonList(Utils.returnAppointment()));
        when(appointmentMapper.toDTO(anyList())).thenReturn(Collections.singletonList(Utils.returnAppointmentDto()));

        List<AppointmentDTO> allAppointmentsForPatient = appointmentService.getAllAppointmentsForPatient(any());
        assertNotEquals(0, allAppointmentsForPatient.size());
    }

    @Test
    public void testGetAllAppointmentsForDoctor() {

        when(appointmentRepository.findAllByDoctorId(any(), any())).thenReturn(Collections.singletonList(Utils.returnAppointment()));
        when(appointmentMapper.toDTO(anyList())).thenReturn(Collections.singletonList(Utils.returnAppointmentDto()));

        List<AppointmentDTO> allAppointmentsForDoctor = appointmentService.getAllAppointmentsForDoctor(eq(any()));
        assertNotEquals(0, allAppointmentsForDoctor.size());
    }

    @Test
    public void testDeleteAppointment() {

        Appointment appointment = Utils.returnAppointment();
        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        appointmentService.deleteAppointment(any());
    }

    @Test
    public void testCreateAppointmentAsDoctor() {

        when(appointmentRepository.findAllByAppointmentDateBetweenAndDoctorId(any(), any(), any())).thenReturn(Collections.emptyList());
        when(appointmentMapper.toDTO(any(Appointment.class))).thenReturn(Utils.returnAppointmentDto());

        Appointment appointment = Utils.returnAppointment();
        Instant instant = Instant.parse("2023-04-04T18:10:00Z");
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        appointment.setAppointmentDate(dateTime);

        securityUtilsMockedStatic.when(SecurityUtils::getCurrentUser).thenReturn(Utils.createEmployeeUser());
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(Utils.createDoctor()));

        when(appointmentRepository.findAllByDoctorId(any(), any())).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any())).thenReturn(Utils.returnAppointment());

        AppointmentDTO appointmentDTO = appointmentService.createAppointmentAsDoctor(Utils.formatDate(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)), "details");
        assertNotNull(appointmentDTO);
    }
}
