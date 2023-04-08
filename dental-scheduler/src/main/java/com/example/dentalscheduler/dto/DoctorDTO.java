package com.example.dentalscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DoctorDTO(
        Long id,

        @NotBlank
        @NotNull
        String firstName,

        @NotBlank
        @NotNull
        String lastName,

        @NotBlank
        @NotNull
        String phoneNumber,

        @NotBlank
        @NotNull
        String CNP,

        List<AppointmentDTO> appointments) {
}
