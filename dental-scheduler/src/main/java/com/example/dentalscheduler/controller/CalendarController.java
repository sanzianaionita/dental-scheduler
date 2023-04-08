package com.example.dentalscheduler.controller;

import com.example.dentalscheduler.dto.CalendarView;
import com.example.dentalscheduler.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar")
@AllArgsConstructor
@Tag(name = "Calendar Controller", description = "Api used for getting a full list of appointments in a calendar, and for getting a list of appointments for a doctor.")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "Get a calendar with all appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Calendar with all appointments",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/full")
    public ResponseEntity<CalendarView> getFullCalendar() {

        CalendarView fullCalendar = calendarService.getFullCalendar();
        return ResponseEntity.ok(fullCalendar);
    }

    @Operation(summary = "Get a calendar with all appointments for doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Calendar with all appointments for doctor",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Not authorized",
                    content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/doctor")
    public ResponseEntity<CalendarView> getCalendarForDoctor(@RequestParam Long doctorId) {

        CalendarView calendarForDoctor = calendarService.getCalendarForDoctor(doctorId);
        return ResponseEntity.ok(calendarForDoctor);
    }
}
