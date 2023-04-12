package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.service.AppointmentService;
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
@RequestMapping("/appointment")
@AllArgsConstructor
@Tag(name = "Appointment Controller", description = "Api used to query a list of appointments, edit, delete and create appointment, get all the appointments for a doctor or a patient and " +
        "create an appointment as a doctor.")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Fetch all appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of all appointments",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {

        List<AppointmentDTO> allAppointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(allAppointments);
    }

    @Operation(summary = "Create an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Appointment created",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestParam Long doctorId,
                                                            @RequestParam String appointmentDate,
                                                            @RequestParam String appointmentDetails) {

        return ResponseEntity.ok(appointmentService.createAppointment(doctorId, appointmentDate, appointmentDetails));
    }

    @Operation(summary = "Create an appointment as doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Appointment created for doctor",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PostMapping("/as-doctor")
    public ResponseEntity<AppointmentDTO> createAppointmentAsDoctor(@RequestParam String appointmentDate,
                                                                    @RequestParam String appointmentDetails) {

        return ResponseEntity.ok(appointmentService.createAppointmentAsDoctor(appointmentDate, appointmentDetails));
    }

    @Operation(summary = "Edit an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "New appointment",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT', 'EMPLOYEE')")
    @PutMapping
    public ResponseEntity<AppointmentDTO> editAppointment(@RequestBody AppointmentDTO appointmentDTO) {

        AppointmentDTO appointmentDTO1 = appointmentService.editAppointment(appointmentDTO);
        return ResponseEntity.ok(appointmentDTO1);
    }

    @Operation(summary = "Fetch all appointments for a patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of all appointments for a patient",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    @GetMapping("/patients")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointmentsForPatients(@RequestParam Long patientId) {

        List<AppointmentDTO> allAppointmentsForPatients = appointmentService.getAllAppointmentsForPatient(patientId);
        return ResponseEntity.ok(allAppointmentsForPatients);
    }

    @Operation(summary = "Fetch all appointments for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of all appointments for a doctor",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/doctors")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointmentsForDoctor(@RequestParam Long doctorId) {

        List<AppointmentDTO> allAppointmentsForDoctors = appointmentService.getAllAppointmentsForDoctor(doctorId);
        return ResponseEntity.ok(allAppointmentsForDoctors);
    }

    @Operation(summary = "Delete an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Appointment deleted",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT', 'EMPLOYEE')")
    @DeleteMapping
    public ResponseEntity<Void> deleteAppointment(@RequestParam Long appointmentId) {

        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }
}
