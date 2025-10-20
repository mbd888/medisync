import api from './axiosConfig';

export const authApi = {
    // Register a new user
    register: async (email, password, role) => {
        const response = await api.post('/auth/register', {
            email,
            password,
            role,
        });
        return response.data;
    },

    // Login user
    login: async (email, password) => {
        const response = await api.post('/auth/login', {
            email,
            password,
        });
        return response.data;
    },
};
