package com.doctor_service.controller;

import com.doctor_service.entity.Doctor;
import com.doctor_service.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
public class SearchController {

    @Autowired
    private DoctorRepository doctorRepository;

    // ✅ Search doctors by specialization and area
    // Example: http://localhost:8080/api/v1/doctor/search/area?specialization=cardiologist&areaName=BTM
    @GetMapping("/search/area")
    public ResponseEntity<List<Doctor>> searchBySpecializationAndArea(
            @RequestParam String specialization,
            @RequestParam String areaName
    ) {
        List<Doctor> doctors = doctorRepository.findBySpecializationAndArea(specialization, areaName);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    // ✅ Search doctors by specialization and city
    // Example: http://localhost:8080/api/v1/doctor/search/city?specialization=cardiologist&cityName=Bengaluru
    @GetMapping("/search/city")
    public ResponseEntity<List<Doctor>> searchBySpecializationAndCity(
            @RequestParam String specialization,
            @RequestParam String cityName
    ) {
        List<Doctor> doctors = doctorRepository.findBySpecializationAndCity(specialization, cityName);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }
}


