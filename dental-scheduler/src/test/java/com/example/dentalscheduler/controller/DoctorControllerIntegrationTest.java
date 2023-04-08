package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.security.util.SecurityUtils;
import com.example.dentalscheduler.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "integrationTest")
class DoctorControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorService doctorService;

    private static MockedStatic<SecurityUtils> securityUtils;

    @BeforeAll
    public static void setup() {
        securityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void tearDown() {
        securityUtils.close();
    }

    @Test
    public void testGetAllDoctors_expect200AndEmptyBody() throws Exception {

        mockMvc
                .perform(get("/doctor/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    public void testGetAllDoctors_expect200AndObject() throws Exception {

        DoctorDTO doctorDTO = createMockedDoctor();
        DoctorDTO doctor = doctorService.createDoctor(doctorDTO);

        mockMvc
                .perform(get("/doctor/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(doctor.id()))
                .andExpect(jsonPath("$[0].firstName").value(doctor.firstName()))
                .andExpect(jsonPath("$[0].lastName").value(doctor.lastName()))
                .andExpect(jsonPath("$[0].phoneNumber").value(doctor.phoneNumber()))
                .andExpect(jsonPath("$[0].CNP").value(doctor.CNP()))
                .andExpect(jsonPath("$[0].appointments", hasSize(0)));

        doctorService.deleteDoctor(doctor.id());
    }

    @Test
    public void testEditDetailsOfDoctor() throws Exception {

        DoctorDTO doctorDTO = createMockedDoctor();
        DoctorDTO doctor = doctorService.createDoctor(doctorDTO);

        when(SecurityUtils.getUserId()).thenReturn(doctor.id());

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "changed_first_name");
        requestParams.add("lastName", "changed_last_name");
        requestParams.add("phoneNumber", doctor.phoneNumber());
        requestParams.add("CNP", doctor.CNP());
        requestParams.add("doctorId", String.valueOf(doctor.id()));

        mockMvc
                .perform(put("/doctor")
                        .params(requestParams)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("changed_first_name"))
                .andExpect(jsonPath("$.lastName").value("changed_last_name"))
                .andExpect(jsonPath("$.phoneNumber").value(doctor.phoneNumber()))
                .andExpect(jsonPath("$.CNP").value(doctor.CNP()))
                .andExpect(jsonPath("$.id").value(String.valueOf(doctor.id())));

        doctorService.deleteDoctor(doctor.id());
    }

    @Test
    public void testDeleteDoctor() throws Exception {

        DoctorDTO doctorDTO = createMockedDoctor();
        DoctorDTO doctor = doctorService.createDoctor(doctorDTO);

        mockMvc
                .perform(delete("/doctor")
                        .param("doctorId", String.valueOf(doctor.id())
                        ))
                .andExpect(status().isOk());

        List<DoctorDTO> allDoctors = doctorService.getAllDoctors();
        assertEquals(0, allDoctors.size());
    }

    @Test
    @Transactional
    public void testCreateDoctor_expectCreated() throws Exception {

        DoctorDTO mockedDoctor = createMockedDoctor();

        MvcResult mvcResult = mockMvc.perform(post("/doctor").content(objectMapper.writeValueAsString(mockedDoctor))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(mockedDoctor.firstName()))
                .andExpect(jsonPath("$.lastName").value(mockedDoctor.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(mockedDoctor.phoneNumber()))
                .andExpect(jsonPath("$.CNP").value(mockedDoctor.CNP()))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        DoctorDTO doctorDTO = objectMapper.readValue(contentAsString, DoctorDTO.class);

        List<DoctorDTO> allDoctors = doctorService.getAllDoctors();
        assertEquals(1, allDoctors.size());

        doctorService.deleteDoctor(doctorDTO.id());
    }

    @Test
    @Transactional
    public void testCreateDoctor_expectBadRequest() throws Exception {

        DoctorDTO mockedDoctor = createMockedDoctor();
        DoctorDTO createdDoctor = doctorService.createDoctor(mockedDoctor);

        mockMvc.perform(post("/doctor").content(objectMapper.writeValueAsString(mockedDoctor))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomException))
                .andExpect(result -> assertEquals("This doctor already exists!", Objects.requireNonNull((CustomException)result.getResolvedException()).getErrorMessage()));

        doctorService.deleteDoctor(createdDoctor.id());
    }

    private static DoctorDTO createMockedDoctor() {
        return new DoctorDTO(1000L, "first_name", "last_name", "phone_number", "cnp", Collections.emptyList());
    }
}