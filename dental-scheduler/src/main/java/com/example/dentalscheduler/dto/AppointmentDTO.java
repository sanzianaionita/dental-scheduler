package com.example.dentalscheduler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AppointmentDTO(
        Long id,

        @NotBlank
        @NotNull
        String appointmentDetails,

        @NotBlank
        @NotNull
        String doctorName,

        @NotBlank
        @NotNull
        Long doctorId,

        @NotBlank
        @NotNull
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String patientName,

        @NotBlank
        @NotNull
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long patientId,

        @NotNull
        LocalDateTime appointmentDate) implements Serializable {

    public AppointmentDTO(String appointmentDetails, Long doctorId, Long patientId, LocalDateTime appointmentDate) {
        this(null, appointmentDetails, null, doctorId, null, patientId, appointmentDate);
    }

    public AppointmentDTO withDetails(String details) {
        return new AppointmentDTO(id(), details, doctorName(), doctorId(), patientName(), patientId(), appointmentDate());
    }
}

