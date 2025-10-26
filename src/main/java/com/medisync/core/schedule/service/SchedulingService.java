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

    // Create a new schedule for a doctor
    @Transactional
    public DoctorScheduleDTO createSchedule(String doctorEmail, CreateScheduleRequest request) {

        Doctor doctor = doctorRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with email: " + doctorEmail
                ));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

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

    // Get all schedules for a doctor (public access)
    @Transactional(readOnly = true)
    public List<DoctorScheduleDTO> getDoctorSchedules(String doctorEmail) {
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctor_Email(doctorEmail);
        return schedules.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorSchedule> getDoctorSchedulesByDoctorId(Long doctorId) {
        return scheduleRepository.findByDoctor_Id(doctorId);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, String doctorEmail) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Schedule not found with id: " + scheduleId
                ));

        if (!schedule.getDoctor().getEmail().equals(doctorEmail)) {
            throw new SecurityException("You don't have permission to delete this schedule");
        }

        scheduleRepository.delete(schedule);
    }

    // Check if a doctor is available at a specific time
    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime startTime) {

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        DoctorSchedule schedule = scheduleRepository
                .findByDoctor_IdAndDayOfWeekAndIsAvailable(doctorId, dayOfWeek, true)
                .orElseThrow(() -> new DoctorNotAvailableException(
                        "Doctor does not work on " + dayOfWeek
                ));

        if (startTime.isBefore(schedule.getStartTime()) ||
                startTime.isAfter(schedule.getEndTime().minusMinutes(schedule.getSlotDuration()))) {
            throw new DoctorNotAvailableException(
                    "Doctor's working hours are " + schedule.getStartTime() +
                            " to " + schedule.getEndTime()
            );
        }

        return true;
    }

    // Check if a doctor has any conflicting appointments on a specific date
    @Transactional(readOnly = true)
    public boolean hasConflict(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctor_IdAndAppointmentDate(doctorId, date);

        for (Appointment existing : existingAppointments) {
            if (existing.getStatus() == Appointment.AppointmentStatus.CANCELLED ||
                    existing.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
                continue;
            }

            boolean overlaps = startTime.isBefore(existing.getEndTime()) &&
                    endTime.isAfter(existing.getStartTime());

            if (overlaps) {
                throw new ScheduleConflictException(
                        "Time slot " + startTime + " - " + endTime + " is already booked"
                );
            }
        }

        return false;
    }

    // Get all available time slots for a doctor on a specific date
    @Transactional(readOnly = true)
    public List<AvailableSlotDTO> getAvailableSlots(Long doctorId, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        DoctorSchedule schedule = scheduleRepository
                .findByDoctor_IdAndDayOfWeekAndIsAvailable(doctorId, dayOfWeek, true)
                .orElseThrow(() -> new DoctorNotAvailableException(
                        "Doctor does not work on " + dayOfWeek
                ));

        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctor_IdAndAppointmentDate(doctorId, date);

        List<AvailableSlotDTO> slots = new ArrayList<>();
        LocalTime currentTime = schedule.getStartTime();

        while (currentTime.isBefore(schedule.getEndTime())) {
            LocalTime slotEnd = currentTime.plusMinutes(schedule.getSlotDuration());

            if (slotEnd.isAfter(schedule.getEndTime())) {
                break;
            }

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

    // Check if a specific time slot is booked
    private boolean isSlotBooked(LocalTime startTime, LocalTime endTime,
                                 List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED ||
                    appointment.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
                continue;
            }

            boolean overlaps = startTime.isBefore(appointment.getEndTime()) &&
                    endTime.isAfter(appointment.getStartTime());

            if (overlaps) {
                return true;
            }
        }

        return false;
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
