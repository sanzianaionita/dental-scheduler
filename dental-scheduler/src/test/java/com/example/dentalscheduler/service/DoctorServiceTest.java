package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.DoctorMapper;
import com.example.dentalscheduler.mapper.DoctorMapperImpl;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.repository.DoctorRepository;
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
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    private static DoctorMapper doctorMapper;
    private static DoctorRepository doctorRepository;
    private static DoctorService doctorService;
    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @BeforeAll
    public static void setup() {

        doctorMapper = mock(DoctorMapper.class);
        doctorRepository = mock(DoctorRepository.class);
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        doctorService = new DoctorService(doctorMapper, doctorRepository);
    }

    @AfterAll
    public static void tearDown() {
        securityUtilsMockedStatic.close();
    }

    @Test
    public void testGetAllDoctors() {

        when(doctorRepository.findAll()).thenReturn(Collections.singletonList(Utils.createDoctor()));
        when(doctorMapper.toDTO(anyList())).thenReturn(Collections.singletonList(Utils.createDoctorDto()));

        List<DoctorDTO> allDoctors = doctorService.getAllDoctors();
        assertEquals(1, allDoctors.size());
    }

    @Test
    public void testFindById() {

        when(doctorRepository.findById(any())).thenReturn(Optional.of(Utils.createDoctor()));
        when(doctorMapper.toDTO(any(Doctor.class))).thenReturn(Utils.createDoctorDto());

        DoctorDTO byId = doctorService.findById(any());
        assertNotNull(byId);
    }

    @Test
    public void testEditDetailsOfDoctor() {

        when(doctorRepository.findById(any())).thenReturn(Optional.of(Utils.createDoctor()));
        when(doctorMapper.toDTO(any(Doctor.class))).thenReturn(Utils.createDoctorDto());

        securityUtilsMockedStatic.when(SecurityUtils::getUserId).thenReturn(Utils.createDoctor().getId());

        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(Utils.createDoctor()));
        when(doctorRepository.save(any())).thenReturn(Utils.createDoctor());

        DoctorDTO doctorDTO = doctorService.editDetailsOfDoctor("test", "test", "test", "test", 1L);
        assertNotNull(doctorDTO);
    }

    @Test
    public void testEditDetailsOfDoctor_expectThrowsException(){

        when(doctorRepository.findById(any())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () -> {
            doctorService.editDetailsOfDoctor("test", "test", "test", "test", 1L);
        });

        String expectedMessage = "Employee does not exist!";
        String actualMessage = exception.getErrorMessage();

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void testCreateDoctor() {

        when(doctorRepository.findFirstByCNP(any())).thenReturn(Optional.empty());
        when(doctorRepository.save(any())).thenReturn(Utils.createDoctor());
        when(doctorMapper.toEntity(any(DoctorDTO.class))).thenReturn(Utils.createDoctor());
        when(doctorMapper.toDTO(any(Doctor.class))).thenReturn(Utils.createDoctorDto());

        DoctorDTO doctor = doctorService.createDoctor(Utils.createDoctorDto());
        assertNotNull(doctor);
    }
}