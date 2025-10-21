import api from './axiosConfig';

export const appointmentApi = {
    // Get all appointments for current user (patient or doctor)
    getMyAppointments: async () => {
        const response = await api.get('/appointments');
        return response.data;
    },

    // Get specific appointment by ID
    getAppointmentById: async (id) => {
        const response = await api.get(`/appointments/${id}`);
        return response.data;
    },

    // Book new appointment (patient only)
    bookAppointment: async (appointmentData) => {
        const response = await api.post('/appointments', appointmentData);
        return response.data;
    },

    // Cancel appointment
    cancelAppointment: async (id) => {
        const response = await api.delete(`/appointments/${id}`);
        return response.data;
    },

    // Get available slots for a doctor
    getAvailableSlots: async (doctorId, date) => {
        const response = await api.get(`/doctors/${doctorId}/available-slots`, {
            params: { date }
        });
        return response.data;
    },
};
