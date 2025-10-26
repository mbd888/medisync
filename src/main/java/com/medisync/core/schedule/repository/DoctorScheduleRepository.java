package com.medisync.core.schedule.repository;

import com.medisync.core.schedule.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DoctorSchedule entity.
 * Provides methods to query doctor schedules by doctor, day of week, etc.
 */
@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    // Find all schedules for a doctor by their email
    List<DoctorSchedule> findByDoctor_Email(String email);

    // Find by ID
    List<DoctorSchedule> findByDoctor_Id(Long doctorId);

    // Find by day of the week
    Optional<DoctorSchedule> findByDoctor_IdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    // Find by doctor ID, day of week, and availability
    Optional<DoctorSchedule> findByDoctor_IdAndDayOfWeekAndIsAvailable(
            Long doctorId,
            DayOfWeek dayOfWeek,
            Boolean isAvailable
    );
}
