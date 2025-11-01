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
            List<LocalTime> allTimeSlots = new ArrayList<>();

            List<DoctorAppointmentSchedule> schedules = doctor.getAppointmentSchedules();

            for (DoctorAppointmentSchedule schedule : schedules) {
                LocalDate scheduleDate = schedule.getDate();
                LocalTime now=LocalTime.now();     //current time
                List<TimeSlots> timeSlots=timeSlotsRepository.getAllTimeSlots(schedule.getId());

                   for (TimeSlots ts : timeSlots)
                   {
                       LocalTime slotTime=ts.getTime();

                       //if schedule is today -> only future time
                       if (scheduleDate.isEqual(today))
                       {
                           if (slotTime.isAfter(now))
                           {
                               allTimeSlots.add(slotTime);
                           }
                       }
                       //if schedule is in the future ->add all times
                       else if (scheduleDate.isAfter(today)) {

                           allTimeSlots.add(slotTime);
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
            dto.setTimeSlots(allTimeSlots);

            result.add(dto);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

