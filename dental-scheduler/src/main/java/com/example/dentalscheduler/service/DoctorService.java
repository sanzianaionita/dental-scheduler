package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.DoctorMapper;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.repository.DoctorRepository;
import com.example.dentalscheduler.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DoctorService {

    private final DoctorMapper doctorMapper;
    private final DoctorRepository doctorRepository;

    public List<DoctorDTO> getAllDoctors() {

        List<Doctor> all = doctorRepository.findAll();
        return doctorMapper.toDTO(all);
    }

    public DoctorDTO findById(Long doctorId) {

        Optional<Doctor> byId = doctorRepository.findById(doctorId);
        return byId.map(doctorMapper::toDTO).orElse(null);
    }

    public DoctorDTO editDetailsOfDoctor(String firstName, String lastName, String phoneNumber, String CNP, Long doctorId) {

        Optional<Doctor> byId = doctorRepository.findById(doctorId);

        if (byId.isEmpty()) {
            throw new CustomException("Employee does not exist!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        Long userId = SecurityUtils.getUserId();

        if (userId == null) {
            throw new CustomException("Doctor not logged in!", HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value());
        }

        doctorRepository.findByUserId(userId).ifPresent(doctor -> {
            if (!byId.get().getId().equals(doctor.getId())){
                throw new CustomException("Doctor details not visible.", HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value());
            }
        });

        Doctor doctor = byId.get();

        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setPhoneNumber(phoneNumber);
        doctor.setCNP(CNP);

        Doctor saveDoctor = doctorRepository.save(byId.get());
        return doctorMapper.toDTO(saveDoctor);
    }

    public void deleteDoctor(Long doctorId) {

        Optional<Doctor> byId = doctorRepository.findById(doctorId);
        if (byId.isEmpty()) {
            throw new CustomException("Doctor does not exist!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        doctorRepository.deleteById(doctorId);
    }

    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {

        Optional<Doctor> firstByCNP = doctorRepository.findFirstByCNP(doctorDTO.CNP());
        if (firstByCNP.isPresent()) {
            throw new CustomException("This doctor already exists!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        Doctor saveDoctor = doctorRepository.save(doctorMapper.toEntity(doctorDTO));
        return doctorMapper.toDTO(saveDoctor);
    }
}
