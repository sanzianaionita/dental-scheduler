package com.example.dentalscheduler.repository;

import com.example.dentalscheduler.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findFirstByCNP(String CNP);

    Optional<Patient> findByUserId(Long userId);
}
