package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.dto.CalendarView;
import com.example.dentalscheduler.mapper.AppointmentMapperImpl;
import com.example.dentalscheduler.repository.AppointmentRepository;
import com.example.dentalscheduler.repository.DoctorRepository;
import com.example.dentalscheduler.utils.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalendarServiceTest {

    private static AppointmentRepository appointmentRepository;
    private static AppointmentMapperImpl appointmentMapper;
    private static CalendarService calendarService;
    private static DoctorRepository doctorRepository;

    @BeforeAll
    public static void setup() {

        appointmentRepository = mock(AppointmentRepository.class);
        appointmentMapper = mock(AppointmentMapperImpl.class);
        doctorRepository = mock(DoctorRepository.class);
        calendarService = new CalendarService(appointmentRepository, appointmentMapper, doctorRepository);
    }

    @Test
    public void testGetFullCalendar() {

        when(appointmentRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(Utils.returnAppointment()));
        when(appointmentMapper.toDTO(anyList())).thenReturn(Collections.singletonList(Utils.returnAppointmentDto()));

        CalendarView fullCalendar = calendarService.getFullCalendar();
        assertNotNull(fullCalendar.getCalendar());

        Map<LocalDate, Map<String, List<AppointmentDTO>>> calendar = fullCalendar.getCalendar();
        assertEquals(1, calendar.keySet().size());
    }

    @Test
    public void testGetCalendarForDoctor() {

        when(appointmentRepository.findAllByDoctorId(any(), any())).thenReturn(Collections.singletonList(Utils.returnAppointment()));
        when(appointmentMapper.toDTO(anyList())).thenReturn(Collections.singletonList(Utils.returnAppointmentDto()));
        when(doctorRepository.findById(any())).thenReturn(Optional.of(Utils.createDoctor()));

        CalendarView calendarForDoctor = calendarService.getCalendarForDoctor(0L);
        assertNotNull(calendarForDoctor.getCalendar());

        Map<LocalDate, Map<String, List<AppointmentDTO>>> calendar = calendarForDoctor.getCalendar();
        assertEquals(1, calendar.keySet().size());
    }
}