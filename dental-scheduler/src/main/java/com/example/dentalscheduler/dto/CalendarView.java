package com.example.dentalscheduler.dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarView {

    private Map<LocalDate, Map<String, List<AppointmentDTO>>> calendar = new HashMap<>();

    public Map<LocalDate, Map<String, List<AppointmentDTO>>> getCalendar() {
        return calendar;
    }

    public void setCalendar(Map<LocalDate, Map<String, List<AppointmentDTO>>> calendar) {
        this.calendar = calendar;
    }
}
