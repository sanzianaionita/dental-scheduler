package com.example.dentalscheduler.utils;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.enums.Role;
import com.example.dentalscheduler.model.Appointment;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static Appointment returnAppointment() {

        Appointment appointment = new Appointment();

        appointment.setId(1L);
        appointment.setAppointmentDetails("details");
        appointment.setPatient(createPatient());
        appointment.setDoctor(createDoctor());
        appointment.setAppointmentDate(LocalDateTime.now());

        return appointment;
    }

    public static AppointmentDTO returnAppointmentDto() {
        return new AppointmentDTO("details", 1L, 1L, LocalDateTime.now());
    }

    public static Doctor createDoctor() {

        Doctor doctor = new Doctor();

        doctor.setId(1L);
        doctor.setCNP("0987654321");
        doctor.setFirstName("test");
        doctor.setLastName("test");
        doctor.setPhoneNumber("0722111333");

        return doctor;
    }

    public static DoctorDTO createDoctorDto() {

        return new DoctorDTO(1L, "test", "test", "0722111333", "0987654321", null);
    }

    public static Patient createPatient() {

        Patient patient = new Patient();

        patient.setId(1L);
        patient.setCNP("1234567890");
        patient.setFirstName("test");
        patient.setLastName("test");
        patient.setPhoneNumber("0711222333");

        return patient;
    }

    public static PatientDTO createPatientDto() {

        return new PatientDTO(1L, "test", "test", "0711222333", "1234567890", null);
    }

    public static User createClientUser() {

        User user = new User();

        user.setId(1L);
        user.setUsername("test");
        user.setPassword("test");
        user.setRole(Role.CLIENT);

        return user;
    }

    public static User createEmployeeUser() {

        User user = new User();

        user.setId(1L);
        user.setUsername("test");
        user.setPassword("test");
        user.setRole(Role.EMPLOYEE);

        return user;
    }

    public static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return dateTime.format(formatter);
    }
}
