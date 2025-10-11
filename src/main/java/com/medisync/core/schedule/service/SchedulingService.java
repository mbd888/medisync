package com.medisync.core.schedule.service;

import com.medisync.core.appointment.entity.Appointment;
import com.medisync.core.appointment.repository.AppointmentRepository;
import com.medisync.core.exception.DoctorNotAvailableException;
import com.medisync.core.exception.ScheduleConflictException;
import com.medisync.core.schedule.dto.AvailableSlotDTO;
import com.medisync.core.schedule.dto.CreateScheduleRequest;
import com.medisync.core.schedule.dto.DoctorScheduleDTO;
import com.medisync.core.schedule.entity.DoctorSchedule;
import com.medisync.core.schedule.repository.DoctorScheduleRepository;
import com.medisync.core.doctor.entity.Doctor;
import com.medisync.core.doctor.repository.DoctorRepository;
import com.medisync.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for scheduling operations - the brain of smart appointment booking.
 * Handles:
 * - Creating and managing doctor schedules
 * - Checking if doctor is available at a specific time
 * - Detecting appointment conflicts (double-booking)
 * - Calculating available time slots
 */
@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Create a schedule for a doctor.
     *
     * @param doctorEmail doctor's email (from JWT)
     * @param request schedule details
     * @return created schedule DTO
     * @throws ResourceNotFoundException if doctor not found
     */
    @Transactional
    public DoctorScheduleDTO createSchedule(String doctorEmail, CreateScheduleRequest request) {
        // Find doctor
        Doctor doctor = doctorRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with email: " + doctorEmail
                ));

        // Validate times
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Create schedule
        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .slotDuration(request.getSlotDuration())
                .isAvailable(true)
                .build();

        DoctorSchedule savedSchedule = scheduleRepository.save(schedule);
        return mapToDTO(savedSchedule);
    }

    /**
     * Get all schedules for a doctor.
     *
     * @param doctorEmail doctor's email
     * @return list of schedules
     */
    @Transactional(readOnly = true)
    public List<DoctorScheduleDTO> getDoctorSchedules(String doctorEmail) {
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctor_Email(doctorEmail);
        return schedules.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete a schedule.
     *
     * @param scheduleId schedule ID
     * @param doctorEmail doctor's email (for security check)
     */
    @Transactional
    public void deleteSchedule(Long scheduleId, String doctorEmail) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Schedule not found with id: " + scheduleId
                ));

        // Security check: only the schedule owner can delete
        if (!schedule.getDoctor().getEmail().equals(doctorEmail)) {
            throw new SecurityException("You don't have permission to delete this schedule");
        }

        scheduleRepository.delete(schedule);
    }

    /**
     * Check if doctor is available on a specific date and time.
     *
     * @param doctorId doctor's ID
     * @param date appointment date
     * @param startTime appointment start time
     * @return true if available, false otherwise
     * @throws DoctorNotAvailableException if doctor doesn't work at this time
     */
    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime startTime) {
        // Get day of week from date
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Find doctor's schedule for this day
        DoctorSchedule schedule = scheduleRepository
                .findByDoctor_IdAndDayOfWeekAndIsAvailable(doctorId, dayOfWeek, true)
                .orElseThrow(() -> new DoctorNotAvailableException(
                        "Doctor does not work on " + dayOfWeek
                ));

        // Check if time is within working hours
        if (startTime.isBefore(schedule.getStartTime()) ||
                startTime.isAfter(schedule.getEndTime().minusMinutes(schedule.getSlotDuration()))) {
            throw new DoctorNotAvailableException(
                    "Doctor's working hours are " + schedule.getStartTime() +
                            " to " + schedule.getEndTime()
            );
        }

        return true;
    }

    /**
     * Check if there's a scheduling conflict (double-booking).
     *
     * @param doctorId doctor's ID
     * @param date appointment date
     * @param startTime appointment start time
     * @param endTime appointment end time
     * @return true if there's a conflict
     * @throws ScheduleConflictException if time slot is already booked
     */
    @Transactional(readOnly = true)
    public boolean hasConflict(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Get all appointments for this doctor on this date
        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctor_IdAndAppointmentDate(doctorId, date);

        // Check for time overlap with any existing appointment
        for (Appointment existing : existingAppointments) {
            // Skip canceled or no-show appointments
            if (existing.getStatus() == Appointment.AppointmentStatus.CANCELLED ||
                    existing.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
                continue;
            }

            // Check if times overlap
            boolean overlaps = startTime.isBefore(existing.getEndTime()) &&
                    endTime.isAfter(existing.getStartTime());

            if (overlaps) {
                throw new ScheduleConflictException(
                        "Time slot " + startTime + " - " + endTime + " is already booked"
                );
            }
        }

        return false; // No conflict
    }

    /**
     * Get available time slots for a doctor on a specific date.
     *
     * @param doctorId doctor's ID
     * @param date the date to check
     * @return list of available slots
     */
    @Transactional(readOnly = true)
    public List<AvailableSlotDTO> getAvailableSlots(Long doctorId, LocalDate date) {
        // Get day of week
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Find doctor's schedule for this day
        DoctorSchedule schedule = scheduleRepository
                .findByDoctor_IdAndDayOfWeekAndIsAvailable(doctorId, dayOfWeek, true)
                .orElseThrow(() -> new DoctorNotAvailableException(
                        "Doctor does not work on " + dayOfWeek
                ));

        // Get existing appointments
        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctor_IdAndAppointmentDate(doctorId, date);

        // Generate all possible time slots
        List<AvailableSlotDTO> slots = new ArrayList<>();
        LocalTime currentTime = schedule.getStartTime();

        while (currentTime.isBefore(schedule.getEndTime())) {
            LocalTime slotEnd = currentTime.plusMinutes(schedule.getSlotDuration());

            // Don't create slot if it goes past end time
            if (slotEnd.isAfter(schedule.getEndTime())) {
                break;
            }

            // Check if this slot is available
            boolean isBooked = isSlotBooked(currentTime, slotEnd, existingAppointments);

            slots.add(AvailableSlotDTO.builder()
                    .startTime(currentTime)
                    .endTime(slotEnd)
                    .isAvailable(!isBooked)
                    .build());

            currentTime = slotEnd;
        }

        return slots;
    }

    /**
     * Check if a specific time slot is already booked.
     *
     * @param startTime slot start time
     * @param endTime slot end time
     * @param appointments list of existing appointments
     * @return true if slot is booked, false if available
     */
    private boolean isSlotBooked(LocalTime startTime, LocalTime endTime,
                                 List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            // Skip canceled or no-show appointments
            if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED ||
                    appointment.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
                continue;
            }

            // Check if times overlap
            boolean overlaps = startTime.isBefore(appointment.getEndTime()) &&
                    endTime.isAfter(appointment.getStartTime());

            if (overlaps) {
                return true; // Slot is booked
            }
        }

        return false; // Slot is available
    }

    /**
     * Convert DoctorSchedule entity to DTO.
     */
    private DoctorScheduleDTO mapToDTO(DoctorSchedule schedule) {
        return DoctorScheduleDTO.builder()
                .id(schedule.getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .slotDuration(schedule.getSlotDuration())
                .isAvailable(schedule.getIsAvailable())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
