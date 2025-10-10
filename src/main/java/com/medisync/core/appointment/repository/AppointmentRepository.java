package com.medisync.core.appointment.repository;

import com.medisync.core.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Appointment entity.
 * Spring Data JPA provides standard CRUD methods automatically.
 * Custom query methods are auto-implemented based on naming convention.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find all appointments for a patient by their email.
     * Uses nested property access: patient.email
     *
     * @param email patient's email
     * @return list of appointments
     */
    List<Appointment> findByPatient_Email(String email);

    /**
     * Find all appointments for a doctor by their email.
     * Uses nested property access: doctor.email
     *
     * @param email doctor's email
     * @return list of appointments
     */
    List<Appointment> findByDoctor_Email(String email);

    /**
     * Find all appointments for a patient by patient ID.
     *
     * @param patientId patient's ID
     * @return list of appointments
     */
    List<Appointment> findByPatient_Id(Long patientId);

    /**
     * Find all appointments for a doctor by doctor ID.
     *
     * @param doctorId doctor's ID
     * @return list of appointments
     */
    List<Appointment> findByDoctor_Id(Long doctorId);

    /**
     * Find all appointments on a specific date for a doctor.
     * Useful for checking doctor's schedule.
     *
     * @param doctorId doctor's ID
     * @param date the appointment date
     * @return list of appointments
     */
    List<Appointment> findByDoctor_IdAndAppointmentDate(Long doctorId, LocalDate date);

    /**
     * Find all appointments with a specific status for a patient.
     *
     * @param email patient's email
     * @param status appointment status
     * @return list of appointments
     */
    List<Appointment> findByPatient_EmailAndStatus(String email, Appointment.AppointmentStatus status);

    /**
     * Find all appointments with a specific status for a doctor.
     *
     * @param email doctor's email
     * @param status appointment status
     * @return list of appointments
     */
    List<Appointment> findByDoctor_EmailAndStatus(String email, Appointment.AppointmentStatus status);
}
