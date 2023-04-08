package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.security.util.SecurityUtils;
import com.example.dentalscheduler.service.PatientService;
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
public class PatientControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

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
    public void testGetAllPatients_expect200AndEmptyBody() throws Exception {

        mockMvc
                .perform(get("/patient/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    public void testGetAllPatients_expect200AndObject() throws Exception {

        PatientDTO patientDTO = createMockedPatient();
        PatientDTO patient = patientService.createPatient(patientDTO);

        mockMvc
                .perform(get("/patient/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(patient.id()))
                .andExpect(jsonPath("$[0].firstName").value(patient.firstName()))
                .andExpect(jsonPath("$[0].lastName").value(patient.lastName()))
                .andExpect(jsonPath("$[0].phoneNumber").value(patient.phoneNumber()))
                .andExpect(jsonPath("$[0].CNP").value(patient.CNP()))
                .andExpect(jsonPath("$[0].appointments", hasSize(0)));

        patientService.deletePatient(patient.id());
    }

    @Test
    public void testEditDetailsOfPatient() throws Exception {

        PatientDTO patientDTO = createMockedPatient();
        PatientDTO patient = patientService.createPatient(patientDTO);

        when(SecurityUtils.getUserId()).thenReturn(patient.id());

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "changed_first_name");
        requestParams.add("lastName", "changed_last_name");
        requestParams.add("phoneNumber", patient.phoneNumber());
        requestParams.add("CNP", patient.CNP());
        requestParams.add("patientId", String.valueOf(patient.id()));

        mockMvc
                .perform(put("/patient")
                        .params(requestParams)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("changed_first_name"))
                .andExpect(jsonPath("$.lastName").value("changed_last_name"))
                .andExpect(jsonPath("$.phoneNumber").value(patient.phoneNumber()))
                .andExpect(jsonPath("$.CNP").value(patient.CNP()))
                .andExpect(jsonPath("$.id").value(String.valueOf(patient.id())));

        patientService.deletePatient(patient.id());
    }

    @Test
    public void testDeletePatient() throws Exception {

        PatientDTO patientDTO = createMockedPatient();
        PatientDTO patient = patientService.createPatient(patientDTO);

        mockMvc
                .perform(delete("/patient")
                        .param("patientId", String.valueOf(patient.id())
                        ))
                .andExpect(status().isOk());

        List<PatientDTO> allPatients = patientService.getAllPatients();
        assertEquals(0, allPatients.size());
    }

    @Test
    @Transactional
    public void testCreatePatient_expectBadRequest() throws Exception {

        PatientDTO mockedPatient = createMockedPatient();
        PatientDTO patient = patientService.createPatient(mockedPatient);

        mockMvc.perform(post("/patient").content(objectMapper.writeValueAsString(mockedPatient))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomException))
                .andExpect(result -> assertEquals("This patient already exists!", Objects.requireNonNull((CustomException) result.getResolvedException()).getErrorMessage()));

        patientService.deletePatient(patient.id());
    }

    @Test
    @Transactional
    public void testCreatePatient_expectCreated() throws Exception {

        PatientDTO mockedPatient = createMockedPatient();

        MvcResult mvcResult = mockMvc.perform(post("/patient").content(objectMapper.writeValueAsString(mockedPatient))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(mockedPatient.firstName()))
                .andExpect(jsonPath("$.lastName").value(mockedPatient.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(mockedPatient.phoneNumber()))
                .andExpect(jsonPath("$.CNP").value(mockedPatient.CNP()))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        PatientDTO patientDTO = objectMapper.readValue(contentAsString, PatientDTO.class);

        List<PatientDTO> allPatients = patientService.getAllPatients();
        assertEquals(1, allPatients.size());

        patientService.deletePatient(patientDTO.id());
    }

    private static PatientDTO createMockedPatient() {
        return new PatientDTO(1000L, "first_name", "last_name", "phone_number", "cnp", Collections.emptyList());
    }
}