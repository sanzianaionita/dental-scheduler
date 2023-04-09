package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.enums.Role;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.DoctorMapper;
import com.example.dentalscheduler.mapper.PatientMapper;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.model.User;
import com.example.dentalscheduler.repository.DoctorRepository;
import com.example.dentalscheduler.repository.PatientRepository;
import com.example.dentalscheduler.repository.UserRepository;
import com.example.dentalscheduler.security.CustomUserDetails;
import com.example.dentalscheduler.security.config.ConfigClass;
import com.example.dentalscheduler.security.jwt.JwtTokenUtil;
import com.example.dentalscheduler.security.jwt.dto.TokenDetails;
import com.example.dentalscheduler.security.service.CustomUserDetailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Profile(value = "!integrationTest")
public class AuthenticationService {

    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ConfigClass configClass;
    private final DoctorMapper doctorMapper;
    private final DoctorRepository doctorRepository;
    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;


    public TokenDetails login(String username, String password) throws Exception {
        authenticate(username, password);

        return authenticateAndRetrieveTokenDetails(username);
    }

    private TokenDetails authenticateAndRetrieveTokenDetails(String username) {

        CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);

        return jwtTokenUtil.getTokenDetails(token);
    }

    private void authenticate(String userName, String password) throws Exception {

        Objects.requireNonNull(userName);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        } catch (DisabledException e) {
            log.error("User {} is disabled", userName);
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new CustomException("Bad username or password!", HttpStatus.BAD_REQUEST,  HttpStatus.BAD_REQUEST.value());
        }
    }

    @Transactional
    public TokenDetails registerDoctor(String username, String password, DoctorDTO doctorDTO) {

        Optional<User> byUsername = userRepository.findByUsername(username);

        if (byUsername.isPresent()) {
            throw new CustomException("User already exists!", HttpStatus.BAD_REQUEST,  HttpStatus.BAD_REQUEST.value());
        }

        User user = new User();
        user.setRole(Role.EMPLOYEE);
        user.setPassword(configClass.passwordEncoder().encode(password));
        user.setUsername(username);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        doctor.setUser(savedUser);

        doctorRepository.save(doctor);

        CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);
        return jwtTokenUtil.getTokenDetails(token);
    }

    @Transactional
    public TokenDetails registerPatient(String username, String password, PatientDTO patientDTO) {

        Optional<User> byUsername = userRepository.findByUsername(username);

        if (byUsername.isPresent()) {
            throw new CustomException("User already exists!", HttpStatus.BAD_REQUEST,  HttpStatus.BAD_REQUEST.value());
        }

        User user = new User();
        user.setRole(Role.CLIENT);
        user.setPassword(configClass.passwordEncoder().encode(password));
        user.setUsername(username);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Patient patient = patientMapper.toEntity(patientDTO);
        patient.setUser(savedUser);

        patientRepository.save(patient);

        CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);
        return jwtTokenUtil.getTokenDetails(token);
    }
}
