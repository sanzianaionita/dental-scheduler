package com.example.dentalscheduler.repository;

import com.example.dentalscheduler.model.Appointment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByAppointmentDateBetweenAndDoctorIdAndPatientId(LocalDateTime startDate, LocalDateTime endDate, Long doctorId, Long patientId);

    List<Appointment> findAllByAppointmentDateBetweenAndDoctorId(LocalDateTime startDate, LocalDateTime endDate, Long doctorId);

    List<Appointment> findAllByAppointmentDateBetweenAndPatientId(LocalDateTime startDate, LocalDateTime endDate, Long patientId);

    List<Appointment> findAllByPatientId(Long patientId);

    List<Appointment> findAllByDoctorId(Long doctorId, Sort sort);
}
