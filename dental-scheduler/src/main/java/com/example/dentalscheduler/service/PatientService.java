package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.PatientMapper;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.repository.PatientRepository;
import com.example.dentalscheduler.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PatientService {

    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;

    public List<PatientDTO> getAllPatients() {

        List<Patient> all = patientRepository.findAll();
        return patientMapper.toDTO(all);
    }

    public PatientDTO findById(Long patientId) {

        Optional<Patient> byId = patientRepository.findById(patientId);
        return byId.map(patientMapper::toDTO).orElse(null);
    }

    public PatientDTO editDetailsOfPatient(String firstName, String lastName, String phoneNumber, String CNP, Long patientId) {

        Optional<Patient> byId = patientRepository.findById(patientId);

        if (byId.isEmpty()) {
            throw new CustomException("Patient does not exist!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        Long userId = SecurityUtils.getUserId();

        if (userId == null) {
            throw new CustomException("User not logged in!", HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value());
        }

        patientRepository.findByUserId(userId).ifPresent(patient -> {
            if (!byId.get().getId().equals(patient.getId())) {
                throw new CustomException("Can't change the details of another account.", HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value());
            }
        });

        Patient patient = byId.get();

        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setPhoneNumber(phoneNumber);
        patient.setCNP(CNP);

        Patient savePatient = patientRepository.save(byId.get());
        return patientMapper.toDTO(savePatient);
    }

    public void deletePatient(Long patientId) {

        Optional<Patient> byId = patientRepository.findById(patientId);
        if (byId.isEmpty()) {
            throw new CustomException("Patient does not exist!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        patientRepository.deleteById(patientId);
    }

    public PatientDTO createPatient(PatientDTO patientDTO) {

        Optional<Patient> firstByEmail = patientRepository.findFirstByCNP(patientDTO.CNP());
        if (firstByEmail.isPresent()) {
            throw new CustomException("This patient already exists!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        Patient savePatient = patientRepository.save(patientMapper.toEntity(patientDTO));
        return patientMapper.toDTO(savePatient);
    }
}
