package com.medisync.core.doctor.entity;

import com.medisync.core.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// Doctor entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String specialization; // Cardiology, Neurology, Pediatrics, etc.

    @Column(name = "license_number", unique = true, length = 50)
    private String licenseNumber;

    @Column(length = 200)
    private String qualification; // MD, MBBS, DO, etc.

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(length = 1000)
    private String bio; // Short description about the doctor
}
