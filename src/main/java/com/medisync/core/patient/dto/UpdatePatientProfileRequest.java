package com.medisync.core.patient.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for updating patient profile.
 * Patient sends this when updating their profile:
 * PUT /api/patients/profile
 * All fields are optional - patient can update only what they want.
 * Validation ensures data quality when provided.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientProfileRequest {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Phone must be 10-20 digits")
    private String phone;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Pattern(regexp = "A\\+|A-|B\\+|B-|AB\\+|AB-|O\\+|O-", message = "Invalid blood type")
    private String bloodType;

    @Size(max = 500, message = "Allergies must not exceed 500 characters")
    private String allergies;

    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Emergency contact phone must be 10-20 digits")
    private String emergencyContactPhone;

    @Size(max = 100, message = "Insurance provider must not exceed 100 characters")
    private String insuranceProvider;

    @Size(max = 50, message = "Insurance policy number must not exceed 50 characters")
    private String insurancePolicyNumber;
}
