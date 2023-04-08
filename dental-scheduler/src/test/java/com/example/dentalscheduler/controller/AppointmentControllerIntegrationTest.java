package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.mapper.AppointmentMapper;
import com.example.dentalscheduler.model.Appointment;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.model.User;
import com.example.dentalscheduler.repository.AppointmentRepository;
import com.example.dentalscheduler.repository.DoctorRepository;
import com.example.dentalscheduler.repository.PatientRepository;
import com.example.dentalscheduler.security.util.SecurityUtils;
import com.example.dentalscheduler.service.AppointmentService;
import com.example.dentalscheduler.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "integrationTest")
class AppointmentControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private static MockedStatic<SecurityUtils> securityUtils;

    @BeforeAll
    public static void setup() {
        securityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void tearDown() {
        securityUtils.close();
    }

    @Test
    public void testGetAllAppointments_expect200AndEmptyBody() throws Exception {

        mockMvc
                .perform(get("/appointment/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    @Transactional
    public void testGetAllAppointments_expect200AndObject() throws Exception {

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
        appointment.setAppointmentDetails("test_details");
        Appointment savedAppointment = appointmentRepository.save(appointment);

        LocalDateTime expectedDateTime = savedAppointment.getAppointmentDate();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String expectedDateTimeStr = expectedDateTime.format(formatter);

        mockMvc
                .perform(get("/appointment/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(savedAppointment.getId()))
                .andExpect(jsonPath("$[0].appointmentDetails").value(savedAppointment.getAppointmentDetails()))
                .andExpect(jsonPath("$[0].appointmentDate").value(expectedDateTimeStr));

        appointmentService.deleteAppointment(savedAppointment.getId());
    }

    @Test
    @Transactional
    public void testCreateAppointment_expectCreated() throws Exception {

        Patient patient = Utils.createPatient();
        User clientUser = Utils.createClientUser();
        patient.setUser(clientUser);
        when(SecurityUtils.getCurrentUser()).thenReturn(clientUser);
        patientRepository.save(patient);

        AppointmentDTO mockedAppointment = createMockedAppointment();

        LocalDateTime expectedDateTime = mockedAppointment.appointmentDate();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.MILLI_OF_SECOND, 2, 5, true)
                .toFormatter();
        String expectedDateTimeStr = expectedDateTime.format(formatter);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("doctorId", mockedAppointment.doctorId().toString());
        requestParams.add("appointmentDate", Utils.formatDate(expectedDateTime));
        requestParams.add("appointmentDetails", "appointment_details");

        MvcResult mvcResult = mockMvc.perform(post("/appointment").params(requestParams))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.appointmentDate").value(expectedDateTimeStr))
                .andExpect(jsonPath("$.appointmentDetails").value("appointment_details"))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        AppointmentDTO appointmentDTO = objectMapper.readValue(contentAsString, AppointmentDTO.class);

        List<AppointmentDTO> allAppointments = appointmentService.getAllAppointments();
        assertEquals(1, allAppointments.size());

        appointmentService.deleteAppointment(appointmentDTO.id());
    }

    @Test
    @Transactional
    public void testCreateAppointmentAsDoctor() throws Exception {

        Doctor doctor = Utils.createDoctor();
        User employeeUser = Utils.createEmployeeUser();
        doctor.setUser(employeeUser);
        when(SecurityUtils.getCurrentUser()).thenReturn(employeeUser);
        doctorRepository.save(doctor);

        AppointmentDTO mockedAppointment = createMockedAppointment();

        LocalDateTime expectedDateTime = mockedAppointment.appointmentDate();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.MILLI_OF_SECOND, 3, 3, true)
                .toFormatter();
        String expectedDateTimeStr = expectedDateTime.format(formatter);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("appointmentDate", Utils.formatDate(expectedDateTime));
        requestParams.add("appointmentDetails", "appointment_details");

        MvcResult mvcResult = mockMvc.perform(post("/appointment/as-doctor").params(requestParams))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.appointmentDate").value(expectedDateTimeStr))
                .andExpect(jsonPath("$.appointmentDetails").value("appointment_details"))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        AppointmentDTO appointmentDTO = objectMapper.readValue(contentAsString, AppointmentDTO.class);

        List<AppointmentDTO> allAppointments = appointmentService.getAllAppointments();
        assertEquals(1, allAppointments.size());

        appointmentService.deleteAppointment(appointmentDTO.id());
    }

    @Test
    @Transactional
    public void testEditAppointment() throws Exception {

        User clientUser = Utils.createClientUser();
        securityUtils.when(SecurityUtils::getUserId).thenReturn(clientUser.getId());

        AppointmentDTO mockedAppointment = createMockedAppointment();
        Appointment saved = appointmentRepository.save(appointmentMapper.toEntity(mockedAppointment));

        AppointmentDTO changedDetails = appointmentMapper.toDTO(saved).withDetails("changed_details");

        mockMvc
                .perform(
                        put("/appointment")
                                .content(objectMapper.writeValueAsString(changedDetails))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.appointmentDetails").value(changedDetails.appointmentDetails()));

        appointmentRepository.deleteAll();
    }

    @Test
    @Transactional
    public void getAllAppointmentsForPatients() throws Exception {

        Patient patient = createPatientAndSave("first_name", "last_name", "CNP", "phone_number");
        Appointment appointment1 = createAppointmentPatientAndSave("app_1", patient, LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
        Appointment appointment2 = createAppointmentPatientAndSave("app_2", patient, LocalDateTime.now().plus(5, ChronoUnit.MINUTES));

        mockMvc
                .perform(get("/appointment/patients").param("patientId", String.valueOf(patient.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].appointmentDetails").value(appointment1.getAppointmentDetails()))
                .andExpect(jsonPath("$[1].appointmentDetails").value(appointment2.getAppointmentDetails()));

        patientRepository.deleteAll();
    }

    @Test
    @Transactional
    public void getAllAppointmentsForDoctor() throws Exception {

        Doctor doctor = createDoctorAndSave("first_name", "last_name", "CNP", "phone_number");
        Appointment appointment1 = createAppointmentDoctorAndSave("app_1", doctor, LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
        Appointment appointment2 = createAppointmentDoctorAndSave("app_2", doctor, LocalDateTime.now().plus(5, ChronoUnit.MINUTES));

        mockMvc
                .perform(get("/appointment/doctors").param("doctorId", String.valueOf(doctor.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].appointmentDetails").value(appointment1.getAppointmentDetails()))
                .andExpect(jsonPath("$[1].appointmentDetails").value(appointment2.getAppointmentDetails()));

        doctorRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testDeleteAppointment() throws Exception {

        Patient patient = Utils.createPatient();
        User clientUser = Utils.createClientUser();
        patient.setUser(clientUser);
        when(SecurityUtils.getCurrentUser()).thenReturn(clientUser);
        patientRepository.save(patient);

        AppointmentDTO mockedAppointment = createMockedAppointment();
        AppointmentDTO appointment = appointmentService.createAppointment(mockedAppointment.doctorId(), Utils.formatDate(mockedAppointment.appointmentDate()), mockedAppointment.appointmentDetails());

        mockMvc
                .perform(delete("/appointment")
                        .param("appointmentId", String.valueOf(appointment.id())
                        ))
                .andExpect(status().isOk());

        List<AppointmentDTO> allAppointments = appointmentService.getAllAppointments();
        assertEquals(0, allAppointments.size());
    }

    private static AppointmentDTO createMockedAppointment() {
        return new AppointmentDTO(1000L, "test", "doctor_name", 1000L, "patient_name", 1000L, LocalDateTime.now().plusMinutes(5));
    }

    private Doctor createDoctorAndSave(String firstName, String lastName, String CNP, String phoneNumber) {

        Doctor doctor = new Doctor();
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setCNP(CNP);
        doctor.setPhoneNumber(phoneNumber);

        return doctorRepository.save(doctor);
    }

    private Patient createPatientAndSave(String firstName, String lastName, String CNP, String phoneNumber) {

        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setCNP(CNP);
        patient.setPhoneNumber(phoneNumber);

        return patientRepository.save(patient);
    }

    private Appointment createAppointmentDoctorAndSave(String details, Doctor doctor, LocalDateTime appointmentDate) {

        Appointment appointment = new Appointment();
        appointment.setAppointmentDetails(details);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentDate);

        return appointmentRepository.save(appointment);
    }

    private Appointment createAppointmentPatientAndSave(String details, Patient patient, LocalDateTime appointmentDate) {

        Appointment appointment = new Appointment();
        appointment.setAppointmentDetails(details);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(appointmentDate);

        return appointmentRepository.save(appointment);
    }
}