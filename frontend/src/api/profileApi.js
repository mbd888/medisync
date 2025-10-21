import api from './axiosConfig';

export const profileApi = {
    // Get current user's profile (patient or doctor)
    getMyProfile: async (role) => {
        const endpoint = role === 'PATIENT' ? '/patients/profile' : '/doctors/profile';
        const response = await api.get(endpoint);
        return response.data;
    },

    // Update patient profile
    updatePatientProfile: async (profileData) => {
        const response = await api.put('/patients/profile', profileData);
        return response.data;
    },

    // Update doctor profile
    updateDoctorProfile: async (profileData) => {
        const response = await api.put('/doctors/profile', profileData);
        return response.data;
    },

    // Get all doctors (public endpoint)
    getAllDoctors: async () => {
        const response = await api.get('/doctors');
        return response.data;
    },

    // Get doctor's schedule
    getDoctorSchedule: async (doctorId) => {
        const response = await api.get(`/doctors/${doctorId}/schedule`);
        return response.data;
    },
};
