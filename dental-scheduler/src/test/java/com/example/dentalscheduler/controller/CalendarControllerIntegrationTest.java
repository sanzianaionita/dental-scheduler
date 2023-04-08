package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.model.Appointment;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.repository.AppointmentRepository;
import com.example.dentalscheduler.repository.DoctorRepository;
import com.example.dentalscheduler.repository.PatientRepository;
import com.example.dentalscheduler.security.util.SecurityUtils;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "integrationTest")
class CalendarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

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
    public void testGetFullCalendar_expect200AndEmptyBody() throws Exception {

        mockMvc
                .perform(get("/calendar/full"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{}"));
    }

    @Test
    @Transactional
    public void testGetFullCalendar_expect200AndObject() throws Exception {

        Doctor doctor1 = createDoctorAndSave("first_name1", "last_name1", "cnp1", "phone_number1");
        Doctor doctor2 = createDoctorAndSave("first_name2", "last_name2", "cnp2", "phone_number2");
        Patient patient1 = createPatientAndSave("first_name1", "last_name1", "cnp1", "phone_number1");
        Patient patient2 = createPatientAndSave("first_name2", "last_name2", "cnp2", "phone_number2");
        LocalDateTime firstAppointmentDate = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        LocalDateTime secondAppointmentDate = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        Appointment appointment1 = createAppointmentAndSave("details1", doctor1, patient1, firstAppointmentDate);
        Appointment appointment2 = createAppointmentAndSave("details2", doctor2, patient2, secondAppointmentDate);

        mockMvc
                .perform(get("/calendar/full"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.calendar").isMap())
                .andExpect(jsonPath(String.format("$.%s.%s", "calendar", LocalDate.now())).isMap())
                .andExpect(jsonPath(String.format("$.%s.%s.%s", "calendar", LocalDate.now(), firstAppointmentDate.format(DateTimeFormatter.ofPattern("HH:mm"))), hasSize(2)));

        doctorRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    public void testGetCalendarForDoctor() throws Exception {

        Doctor doctor1 = createDoctorAndSave("first_name1", "last_name1", "cnp1", "phone_number1");
        Doctor doctor2 = createDoctorAndSave("first_name2", "last_name2", "cnp2", "phone_number2");
        Patient patient1 = createPatientAndSave("first_name1", "last_name1", "cnp1", "phone_number1");
        Patient patient2 = createPatientAndSave("first_name2", "last_name2", "cnp2", "phone_number2");
        LocalDateTime firstAppointmentDate = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        LocalDateTime secondAppointmentDate = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        Appointment appointment1 = createAppointmentAndSave("details1", doctor1, patient1, firstAppointmentDate);
        Appointment appointment2 = createAppointmentAndSave("details2", doctor2, patient2, secondAppointmentDate);

        mockMvc
                .perform(get("/calendar/doctor").param("doctorId", doctor1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.calendar").isMap())
                .andExpect(jsonPath(String.format("$.%s.%s", "calendar", LocalDate.now())).isMap())
                .andExpect(jsonPath(String.format("$.%s.%s.%s", "calendar", LocalDate.now(), firstAppointmentDate.format(DateTimeFormatter.ofPattern("HH:mm"))), hasSize(1)));

        doctorRepository.deleteAll();
        patientRepository.deleteAll();
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

    private Appointment createAppointmentAndSave(String details, Doctor doctor, Patient patient, LocalDateTime appointmentDate) {

        Appointment appointment = new Appointment();
        appointment.setAppointmentDetails(details);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(appointmentDate);

        return appointmentRepository.save(appointment);
    }
}