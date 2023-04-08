package com.example.dentalscheduler.mapper.resolvers;

import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class DoctorMapperResolver {

    private final DoctorRepository doctorRepository;

    public Doctor findById(Long id){

        Optional<Doctor> byId = doctorRepository.findById(id);
        return byId.orElse(null);
    }
}
