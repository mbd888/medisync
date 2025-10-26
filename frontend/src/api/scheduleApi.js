import api from './axiosConfig';

export const scheduleApi = {

    getMySchedule: async () => {
        const response = await api.get('/doctors/schedule');
        return response.data;
    },

    createSchedule: async (scheduleData) => {
        const response = await api.post('/doctors/schedule', scheduleData);
        return response.data;
    },

    deleteSchedule: async (scheduleId) => {
        const response = await api.delete(`/doctors/schedule/${scheduleId}`);
        return response.data;
    },
};
