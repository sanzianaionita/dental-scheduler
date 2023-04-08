package com.example.dentalscheduler.service;

import com.example.dentalscheduler.dto.AppointmentDTO;
import com.example.dentalscheduler.dto.CalendarView;
import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.mapper.AppointmentMapper;
import com.example.dentalscheduler.model.Appointment;
import com.example.dentalscheduler.model.Doctor;
import com.example.dentalscheduler.repository.AppointmentRepository;
import com.example.dentalscheduler.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CalendarService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorRepository doctorRepository;

    public CalendarView getFullCalendar() {

        CalendarView calendarView = new CalendarView();

        final Sort SORT_ORDER = Sort.by(Sort.Direction.ASC, "appointmentDate");

        List<Appointment> appointments = appointmentRepository.findAll(SORT_ORDER);
        Map<LocalDate, Map<String, List<AppointmentDTO>>> calendar = groupAppointmentsForCalendar(appointments);

        calendarView.setCalendar(calendar);
        return calendarView;
    }

    public CalendarView getCalendarForDoctor(Long doctorId) {

        CalendarView calendarView = new CalendarView();

        Optional<Doctor> byId = doctorRepository.findById(doctorId);
        if (byId.isEmpty()) {
            throw new CustomException("Doctor does not exist!", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }

        final Sort SORT_ORDER = Sort.by(Sort.Direction.ASC, "appointmentDate");

        List<Appointment> allByDoctorId = appointmentRepository.findAllByDoctorId(doctorId, SORT_ORDER);
        Map<LocalDate, Map<String, List<AppointmentDTO>>> calendar = groupAppointmentsForCalendar(allByDoctorId);

        calendarView.setCalendar(calendar);
        return calendarView;
    }

    private Map<LocalDate, Map<String, List<AppointmentDTO>>> groupAppointmentsForCalendar(List<Appointment> appointments) {

        return appointments
                .stream()
                .collect(Collectors.groupingBy(
                        appointment -> appointment.getAppointmentDate().toLocalDate(),
                        Collectors.groupingBy(
                                appointment -> appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("HH:mm")),
                                TreeMap::new,
                                Collectors.mapping(appointmentMapper::toDTO, Collectors.toList()))));
    }
}
