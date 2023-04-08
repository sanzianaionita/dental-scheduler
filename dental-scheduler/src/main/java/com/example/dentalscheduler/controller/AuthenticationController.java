package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.DoctorDTO;
import com.example.dentalscheduler.dto.PatientDTO;
import com.example.dentalscheduler.security.jwt.dto.TokenDetails;
import com.example.dentalscheduler.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Profile(value = "!integrationTest")
@Tag(name = "Authentication Controller", description = "Api used for login and register.")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "You are logged in!",
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
    @PostMapping("/login")
    public ResponseEntity<TokenDetails> login(@RequestParam String username, @RequestParam String password) throws Exception {

        TokenDetails login = authenticationService.login(username, password);
        return ResponseEntity.ok(login);
    }

    @Operation(summary = "Register as doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Account registered",
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
    @PostMapping("/register/doctor")
    public ResponseEntity<TokenDetails> registerDoctor(@RequestParam String username,
                                                       @RequestParam String password,
                                                       @RequestBody DoctorDTO doctorDTO) throws Exception {

        TokenDetails tokenDetails = authenticationService.registerDoctor(username, password, doctorDTO);
        return ResponseEntity.ok(tokenDetails);
    }

    @Operation(summary = "Register as patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Account registered",
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
    @PostMapping("/register/patient")
    public ResponseEntity<TokenDetails> registerPatient(@RequestParam String username,
                                                        @RequestParam String password,
                                                        @RequestBody PatientDTO patientDTO) throws Exception {

        TokenDetails tokenDetails = authenticationService.registerPatient(username, password, patientDTO);
        return ResponseEntity.ok(tokenDetails);
    }
}
