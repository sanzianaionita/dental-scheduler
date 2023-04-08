package com.example.dentalscheduler.dto;

import com.example.dentalscheduler.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
        @NotNull
        Long id,

        @NotBlank
        @NotNull
        String username,

        @NotBlank
        @NotNull
        String password,

        @NotNull
        Role role) {
}
