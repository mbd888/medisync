package com.medisync.core.patient.entity;

import com.medisync.core.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Patient entity extending User.
 * Represents a patient in the healthcare system with medical information.
 * Stored in the same 'users' table using Single Table Inheritance.
 * Medical fields:
 * - Personal: firstName, lastName, dateOfBirth, gender, phone, address
 * - Medical: bloodType, allergies
 * - Emergency: emergencyContactName, emergencyContactPhone
 * - Insurance: insuranceProvider, insurancePolicyNumber
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("PATIENT")
public class Patient extends User {

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender; // MALE, FEMALE, OTHER

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "blood_type", length = 5)
    private String bloodType; // A+, A-, B+, B-, AB+, AB-, O+, O-

    @Column(length = 500)
    private String allergies; // Comma-separated or text description

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "insurance_provider", length = 100)
    private String insuranceProvider;

    @Column(name = "insurance_policy_number", length = 50)
    private String insurancePolicyNumber;
}
