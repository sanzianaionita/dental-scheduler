package com.example.dentalscheduler.mapper.resolvers;

import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PatientMapperResolver {

    private final PatientRepository patientRepository;

    public Patient findById(Long id) {

        if (id == null) {
            return null;
        }

        Optional<Patient> byId = patientRepository.findById(id);
        return byId.orElse(null);
    }
}
