package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.PatientMapper;
import com.example.dentalscheduler.model.Patient;
import com.example.dentalscheduler.repository.PatientRepository;
import com.example.dentalscheduler.repository.UserRepository;
import com.example.dentalscheduler.security.util.SecurityUtils;
import com.example.dentalscheduler.utils.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class PatientServiceTest {

    private static PatientMapper patientMapper;
    private static PatientRepository patientRepository;
    private static PatientService patientService;
    private static UserRepository userRepository;
    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @BeforeAll
    public static void setup() {

        patientMapper = mock(PatientMapper.class);
        patientRepository = mock(PatientRepository.class);
        userRepository = mock(UserRepository.class);
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        patientService = new PatientService(patientMapper, patientRepository, userRepository);
    }


    @AfterAll
    public static void tearDown() {
        securityUtilsMockedStatic.close();
    }


    @Test
    public void testGetAllPatients() {

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(Utils.createPatient()));
        when(patientMapper.toDTO(anyList())).thenReturn(Collections.singletonList(Utils.createPatientDto()));

        List<PatientDTO> allPatients = patientService.getAllPatients();
        assertEquals(1, allPatients.size());
    }

    @Test
    public void testFindById() {

        when(patientRepository.findById(any())).thenReturn(Optional.of(Utils.createPatient()));
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(Utils.createPatientDto());

        PatientDTO byId = patientService.findById(any());
        assertNotNull(byId);
    }

    @Test
    public void testEditDetailsOfPatient() {

        when(patientRepository.findById(any())).thenReturn(Optional.of(Utils.createPatient()));
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(Utils.createPatientDto());

        securityUtilsMockedStatic.when(SecurityUtils::getUserId).thenReturn(Utils.createPatient().getId());

        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(Utils.createPatient()));
        when(patientRepository.save(any())).thenReturn(Utils.createPatient());

        PatientDTO patientDTO = patientService.editDetailsOfPatient("test", "test", "test", "test", 1L);
        assertNotNull(patientDTO);
    }

    @Test
    public void testEditDetailsOfPatient_expectThrowsException() {

        when(patientRepository.findById(any())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () -> {
            patientService.editDetailsOfPatient("test", "test", "test", "test", 1L);
        });

        String expectedMessage = "Patient does not exist!";
        String actualMessage = exception.getErrorMessage();

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void testCreatePatient() {

        when(patientRepository.findFirstByCNP(any())).thenReturn(Optional.empty());
        when(patientRepository.save(any())).thenReturn(Utils.createPatient());
        when(patientMapper.toEntity(any(PatientDTO.class))).thenReturn(Utils.createPatient());
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(Utils.createPatientDto());

        PatientDTO patient = patientService.createPatient(Utils.createPatientDto());
        assertNotNull(patient);
    }
}