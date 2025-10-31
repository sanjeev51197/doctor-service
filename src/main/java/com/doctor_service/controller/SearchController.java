package com.doctor_service.controller;

import com.doctor_service.dto.SearchResultDto;
import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorRepository;
import com.doctor_service.repository.TimeSlotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
public class SearchController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private TimeSlotsRepository timeSlotsRepository;

    // ✅ Search doctors by specialization and area
    // Example: http://localhost:8081/api/v1/doctor/search?specialization=cardiologist&areaName=BTM
    @GetMapping("/search")
    public ResponseEntity<List<SearchResultDto>> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String areaName
    ) {
        LocalDate today = LocalDate.now();
        List<SearchResultDto> result = new ArrayList<>();

        List<Doctor> doctors = doctorRepository.findBySpecializationAndArea(specialization, areaName);

        //new DTO for each doctor
        for (Doctor doctor : doctors) {
            SearchResultDto dto = new SearchResultDto();

            List<LocalDate> validDates = new ArrayList<>();
            List<LocalTime> allTimeSlot = new ArrayList<>();

            List<DoctorAppointmentSchedule> schedules = doctor.getAppointmentSchedules();

            for (DoctorAppointmentSchedule s : schedules) {
                LocalDate scheduleDate = s.getDate();

                //only today or future dates
                if (!scheduleDate.isBefore(today)) {
                    validDates.add(scheduleDate);

                    //all time slots for that schedule
                    List<TimeSlots> timeSlots = timeSlotsRepository.getAllTimeSlots(s.getId());
                    for (TimeSlots ts : timeSlots) {
                        allTimeSlot.add(ts.getTime());
                    }
                }
            }

            //doctor info
            dto.setDoctorId(doctor.getId());
            dto.setName(doctor.getName());
            dto.setQualification(doctor.getQualification());
            dto.setSpecialization(doctor.getSpecialization());
            dto.setArea(doctor.getArea().getName());
            dto.setCity(doctor.getCity().getName());
            dto.setDates(validDates);
            dto.setTimeSlots(allTimeSlot);

            result.add(dto);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

