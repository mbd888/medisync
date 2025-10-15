# MediSync - Smart Healthcare Appointment & Management System

A full-stack healthcare platform built with Spring Boot, demonstrating enterprise-level architecture, security best practices, and intelligent scheduling algorithms.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

## Overview

MediSync is a comprehensive healthcare management system that streamlines the patient-doctor interaction through intelligent appointment scheduling, role-based access control, and secure data management. Built with a focus on security, scalability, and real-world applicability.

**What makes this project special:**
- **Production-ready security** - JWT authentication, BCrypt password hashing, role-based authorization
- **Smart scheduling algorithm** - Conflict detection, doctor availability validation, time slot calculation
- **Clean architecture** - Separation of concerns with DTOs, services, and controllers
- **Enterprise patterns** - Exception handling, audit logging, API versioning
- **HIPAA-inspired compliance** - Data privacy, access control, secure endpoints

---

## Key Features

### 1. **Authentication & Authorization**
- JWT-based stateless authentication
- Secure user registration and login
- Role-based access control (PATIENT, DOCTOR, ADMIN, NURSE)
- Password encryption with BCrypt
- Token refresh mechanism

### 2. **User Profile Management**
- Separate patient and doctor profiles
- Patient medical information (blood type, allergies, emergency contacts)
- Doctor professional details (specialization, license, qualifications)
- Profile update with validation
- Single table inheritance for efficient data modeling

### 3. **Appointment Management**
- Book appointments between patients and doctors
- View appointment history
- Cancel appointments
- Status tracking (SCHEDULED, COMPLETED, CANCELLED, NO_SHOW)
- Role-specific appointment views

### 4. **Smart Scheduling System**
- **Doctor availability schedules** - Doctors set working hours per day of week
- **Conflict detection** - Prevents double-booking automatically
- **Time slot calculation** - Shows available appointment slots in real-time
- **Business rule validation** - Ensures appointments are within working hours
- **Intelligent error handling** - Clear feedback when booking fails

---

## Technical Highlights

### Backend Architecture
```
com.medisync.core/
├── auth/              # Authentication & JWT handling
├── user/              # Base user entity & repository
├── patient/           # Patient-specific features
├── doctor/            # Doctor-specific features
├── appointment/       # Appointment booking & management
├── schedule/          # Smart scheduling algorithms
├── config/            # Security & application configuration
└── exception/         # Global exception handling
```

### Technology Stack

**Core Framework:**
- Spring Boot 3.x
- Spring Security (JWT)
- Spring Data JPA
- Spring Validation

**Database:**
- PostgreSQL 15
- Hibernate ORM
- JPA Auditing

**Security:**
- JSON Web Tokens (JWT)
- BCrypt password hashing
- Role-based access control
- Method-level security with @PreAuthorize

**Development Tools:**
- Maven
- Lombok
- Jackson (JSON serialization)

---

## Architecture

### Layered Architecture Pattern

```
┌─────────────────────────────────────┐
│         REST Controllers            │  ← HTTP layer
├─────────────────────────────────────┤
│            Services                 │  ← Business logic
├─────────────────────────────────────┤
│          Repositories               │  ← Data access
├─────────────────────────────────────┤
│       Database (PostgreSQL)         │  ← Persistence
└─────────────────────────────────────┘
```

### Security Flow

```
Request → JWT Filter → Authentication Check → Authorization Check → Controller → Service → Repository
```

### Scheduling Algorithm

```
Patient books appointment
    ↓
Validate doctor schedule exists for that day
    ↓
Check time is within working hours
    ↓
Query existing appointments for conflicts
    ↓
Detect time slot overlaps
    ↓
Accept or Reject with specific error
```

---

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |

### Patient Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/patients/profile` | Get patient profile | PATIENT |
| PUT | `/api/patients/profile` | Update patient profile | PATIENT |

### Doctor Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/doctors/profile` | Get doctor profile | DOCTOR |
| PUT | `/api/doctors/profile` | Update doctor profile | DOCTOR |
| POST | `/api/doctors/schedule` | Create work schedule | DOCTOR |
| GET | `/api/doctors/schedule` | View my schedules | DOCTOR |
| DELETE | `/api/doctors/schedule/{id}` | Delete schedule | DOCTOR |
| GET | `/api/doctors/{id}/available-slots?date={date}` | View available time slots | Public |

### Appointment Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/appointments` | Book new appointment | PATIENT |
| GET | `/api/appointments` | View my appointments | PATIENT/DOCTOR |
| GET | `/api/appointments/{id}` | View specific appointment | PATIENT/DOCTOR |
| DELETE | `/api/appointments/{id}` | Cancel appointment | PATIENT/DOCTOR |

---

## Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL 15
- Maven 3.8+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/mbd888/medisync.git
cd medisync
```

2. **Configure database**
```bash
# Create PostgreSQL database
sudo -u postgres psql
CREATE DATABASE medisync;
CREATE USER medisync_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE medisync TO medisync_user;
```

3. **Update application.properties with environment variables:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:${DB_PORT)/medisync
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

jwt.secret.key=${JWT_SECRET_KEY}
jwt.expiration=86400000
```

4. **Run the application**
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Quick Test

```bash
# Register a patient
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@test.com",
    "password": "password123",
    "role": "PATIENT"
  }'

# Login and get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@test.com",
    "password": "password123"
  }'
```

---

## Database Schema

### Key Tables

**users** - Base table for all user types (single table inheritance)
- Stores common fields: email, password, role
- Patient-specific fields: blood type, allergies, emergency contacts
- Doctor-specific fields: specialization, license number, qualifications

**appointments** - Appointment records
- Links patients to doctors
- Tracks date, time, status
- Foreign keys to patient and doctor

**doctor_schedules** - Doctor working hours
- Day of week (MONDAY-SUNDAY)
- Start/end times
- Slot duration (default 30 minutes)
- Availability flag

### Entity Relationships

```
User (base)
├── Patient ──1:N→ Appointment ←N:1── Doctor
└── Doctor ──1:N→ DoctorSchedule

Appointment references both Patient and Doctor
DoctorSchedule defines Doctor's working hours
```

---

## Security

### Authentication
- **JWT tokens** with 24-hour expiration
- **Stateless sessions** - no server-side session storage
- **BCrypt password hashing** - industry-standard encryption

### Authorization
- **Role-based access control (RBAC)** - @PreAuthorize annotations
- **Method-level security** - Specific permissions per endpoint
- **Data isolation** - Users can only access their own data

### Best Practices Implemented
- Never expose passwords in API responses  
- Validate all user inputs with @Valid  
- Use DTOs to separate API and database models  
- Implement global exception handling  
- Log security events (audit trail ready)  
- Prevent SQL injection with JPA  
- CORS configuration for frontend integration  

---

## Code Quality Highlights

### Design Patterns Used
- **Repository Pattern** - Data access abstraction
- **DTO Pattern** - API/database separation
- **Builder Pattern** - Clean object creation (Lombok)
- **Singleton Pattern** - Service layer beans
- **Strategy Pattern** - Role-based authorization

### SOLID Principles
- **Single Responsibility** - Each class has one job
- **Open/Closed** - Extensible through inheritance (User → Patient/Doctor)
- **Dependency Inversion** - Services depend on repository interfaces

### Clean Code Practices
- Meaningful variable and method names
- Comprehensive JavaDoc comments
- Consistent error handling
- Input validation at API layer
- Separation of concerns (Controller → Service → Repository)

---

## 🔮 Future Enhancements

### Planned Features
- [ ] **Medical Records System** - Visit notes, prescriptions, lab reports
- [ ] **Notification System** - Email/SMS appointment reminders
- [ ] **Analytics Dashboard** - Appointment statistics and trends
- [ ] **Mobile App** - React Native/Flutter companion

### Technical Improvements
- [ ] Redis caching for performance
- [ ] AWS S3 integration for file storage
- [ ] Comprehensive test suite (JUnit, Mockito)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Docker containerization
- [ ] API rate limiting
- [ ] OpenAPI/Swagger documentation
- [ ] Monitoring with Spring Boot Actuator

---

## What I Learned

I built MediSync to understand what "enterprise-grade" really means beyond just making something that works. This project pushed me to think like an engineer: not just writing code, but architecting systems that could actually handle real users, real data, and real consequences.

The biggest lessons:

**Security isn't optional.** Working with healthcare data made me realize how much thought goes into protecting user information. Implementing JWT properly, understanding why BCrypt matters, and designing role-based authorization taught me that security has to be implemented from day one.
Business logic gets complex fast. The scheduling system looked simple on paper: "book an appointment." But preventing double-bookings, respecting doctor availability, validating time slots made me think through edge cases in depth. This is where I had to leverage creative problem solving.

**Clean architecture pays off.** Since I began Java in AP Computer Science, I would wonder why we need to separate controllers, services, and repositories when one class could do it all. But when I needed to add smart scheduling validation to appointment booking, I just plugged it into the service layer without touching controllers or repositories. That's when the SOLID principles clicked for me.

**DTOs are your friend.** When I added fields to the User entity for patients vs doctors, none of my existing API contracts broke because DTOs acted as a buffer. That separation turned what could have been a messy refactor into a simple, confident change.

**Enterprise Java is different.** Coming into this, I knew Java essentials. But Spring's dependency injection, JPA relationships, transaction management, and method-level security are the tools that let you build systems that scale. I went from "I can write Java" to "I can architect a Spring Boot application."
After writing the conflict detection algorithm, I was satisfied to see it correctly reject a double-booking attempt with a clear error message. I felt that this served a good hands-on lesson of real-world complexity in production systems.

---

## Contributing

This is a portfolio project, but feedback and suggestions are welcome! Feel free to:
- Open an issue for bugs or suggestions
- Fork the repository for your own experimentation
- Reach out with questions about implementation


## License

This project is licensed under the Apache-2.0 License - see the [LICENSE](LICENSE) file for details.

---
