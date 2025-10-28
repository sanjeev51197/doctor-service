package com.doctor_service.repository;

import com.doctor_service.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 🔍 Search by specialization in a particular city (case-insensitive)
    @Query("""
           SELECT d FROM Doctor d
           WHERE LOWER(d.specialization) = LOWER(:specialization)
             AND LOWER(d.city.name) = LOWER(:cityName)
           """)
    List<Doctor> findBySpecializationAndCity(
            @Param("specialization") String specialization,
            @Param("cityName") String cityName);

    // 🔍 Search by specialization in a particular area (case-insensitive)
    @Query("""
           SELECT d FROM Doctor d
           WHERE LOWER(d.specialization) = LOWER(:specialization)
             AND LOWER(d.area.name) = LOWER(:areaName)
           """)
    List<Doctor> findBySpecializationAndArea(
            @Param("specialization") String specialization,
            @Param("areaName") String areaName);
}
