package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.service.DoctorService;
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
@RequestMapping("/doctor")
@AllArgsConstructor
@Tag(name = "Doctor Controller", description = "Api used for query list of doctors, edit, delete and create doctor.")
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "Get all doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returned doctor list",
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
    @GetMapping("/all")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors(){

        List<DoctorDTO> allDoctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(allDoctors);
    }

    @Operation(summary = "Edit doctor details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Doctor details changed",
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PutMapping
    public ResponseEntity<DoctorDTO> editDetailsOfDoctor(@RequestParam String firstName,
                                                         @RequestParam String lastName,
                                                         @RequestParam String phoneNumber,
                                                         @RequestParam String CNP,
                                                         @RequestParam Long doctorId){

        DoctorDTO doctorDTO = doctorService.editDetailsOfDoctor(firstName, lastName, phoneNumber, CNP, doctorId);
        return ResponseEntity.ok(doctorDTO);
    }

    @Operation(summary = "Delete a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Doctor deleted",
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
    public ResponseEntity<Void> deleteDoctor(@RequestParam Long doctorId){

        doctorService.deleteDoctor(doctorId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Create a new doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "New doctor created",
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO doctorDTO){

        DoctorDTO doctor = doctorService.createDoctor(doctorDTO);
        return ResponseEntity.ok(doctor);
    }
}
