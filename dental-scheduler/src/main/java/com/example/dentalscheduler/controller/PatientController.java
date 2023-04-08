package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.service.PatientService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@AllArgsConstructor
@Tag(name = "Patient Controller", description = "Api used for query list of patients, edit, delete and create patient.")
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Get all patients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returned patient list",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad request, adjust before retrying",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {

        List<PatientDTO> allPatients = patientService.getAllPatients();
        return ResponseEntity.ok(allPatients);
    }

    @Operation(summary = "Edit patient details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Patient details changed",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad request, adjust before retrying",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    @PutMapping
    public ResponseEntity<PatientDTO> editDetailsOfPatient(@RequestParam String firstName,
                                                           @RequestParam String lastName,
                                                           @RequestParam String phoneNumber,
                                                           @RequestParam String CNP,
                                                           @RequestParam Long patientId) {

        PatientDTO patientDTO = patientService.editDetailsOfPatient(firstName, lastName, phoneNumber, CNP, patientId);
        return ResponseEntity.ok(patientDTO);
    }

    @Operation(summary = "Delete a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Client deleted",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad request, adjust before retrying",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deletePatient(@RequestParam Long patientId) {

        patientService.deletePatient(patientId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Create a new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "New client created",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad request, adjust before retrying",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)
    })
    @Hidden
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {

        PatientDTO patient = patientService.createPatient(patientDTO);
        return ResponseEntity.ok(patient);
    }
}
