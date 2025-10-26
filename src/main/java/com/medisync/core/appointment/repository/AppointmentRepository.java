package com.medisync.core.appointment.repository;

import com.medisync.core.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Appointment entity.
 * Spring Data JPA provides standard CRUD methods automatically.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find all appointments for a patient/doctor by their email.
     * Uses nested property access: patient.email/doctor.email
     */
    List<Appointment> findByPatient_Email(String email);
    List<Appointment> findByDoctor_Email(String email);

    //Find all appointments for a patient/doctor by ID.
    List<Appointment> findByPatient_Id(Long patientId);
    List<Appointment> findByDoctor_Id(Long doctorId);

    // Find all appointments on a specific date for a doctor.
    List<Appointment> findByDoctor_IdAndAppointmentDate(Long doctorId, LocalDate date);

    // Find all appointments with a specific status for a patient/doctor.
    List<Appointment> findByPatient_EmailAndStatus(String email, Appointment.AppointmentStatus status);
    List<Appointment> findByDoctor_EmailAndStatus(String email, Appointment.AppointmentStatus status);
}
