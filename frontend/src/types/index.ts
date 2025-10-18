export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'DOCTOR' | 'PATIENT' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: 'DOCTOR' | 'PATIENT';
}

export interface DoctorSchedule {
  id: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  slotDuration: number;
  isAvailable: boolean;
}

export interface CreateScheduleRequest {
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  slotDuration: number;
}

export interface AvailableSlot {
  startTime: string;
  endTime: string;
  isAvailable: boolean;
}
