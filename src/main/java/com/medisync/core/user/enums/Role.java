package com.medisync.core.user.enums;

/**
 * Role enum defining the different types of users in the MediSync system.
 * PATIENT - Can book appointments, view their medical records
 * DOCTOR - Can manage appointments, create medical records
 * ADMIN - Can manage users, view system-wide reports
 * NURSE - Can update patient vitals, schedule appointments
 */
public enum Role {
    PATIENT,
    DOCTOR,
    ADMIN,
    NURSE
}
