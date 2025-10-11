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

    /**
     * Find all schedules for a doctor by their email.
     *
     * @param email doctor's email
     * @return list of schedules
     */
    List<DoctorSchedule> findByDoctor_Email(String email);

    /**
     * Find all schedules for a doctor by their ID.
     *
     * @param doctorId doctor's ID
     * @return list of schedules
     */
    List<DoctorSchedule> findByDoctor_Id(Long doctorId);

    /**
     * Find a schedule for a specific doctor on a specific day.
     *
     * @param doctorId doctor's ID
     * @param dayOfWeek day of week
     * @return optional schedule
     */
    Optional<DoctorSchedule> findByDoctor_IdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    /**
     * Find available schedules for a doctor on a specific day.
     * (where isAvailable = true)
     *
     * @param doctorId doctor's ID
     * @param dayOfWeek day of week
     * @param isAvailable availability flag
     * @return optional schedule
     */
    Optional<DoctorSchedule> findByDoctor_IdAndDayOfWeekAndIsAvailable(
            Long doctorId,
            DayOfWeek dayOfWeek,
            Boolean isAvailable
    );
}
