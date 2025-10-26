import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import PatientDashboard from './pages/patient/PatientDashboard';
import DoctorDashboard from './pages/doctor/DoctorDashboard';
import MyAppointments from './pages/patient/MyAppointments';
import BookAppointment from './pages/patient/BookAppointment';
import DoctorAppointments from './pages/doctor/DoctorAppointments';
import ManageSchedule from './pages/doctor/ManageSchedule';

// Protected Route component
function ProtectedRoute({ children, allowedRole }) {
    const { user, loading } = useAuth();

    if (loading) {
        return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
    }

    if (!user) {
        return <Navigate to="/login" />;
    }

    if (allowedRole && user.role !== allowedRole) {
        return <Navigate to="/login" />;
    }

    return children;
}

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Public routes */}
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />

                {/* Patient routes */}
                <Route
                    path="/patient/dashboard"
                    element={
                        <ProtectedRoute allowedRole="PATIENT">
                            <PatientDashboard />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/patient/appointments"
                    element={
                        <ProtectedRoute allowedRole="PATIENT">
                            <MyAppointments />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/patient/book-appointment"
                    element={
                        <ProtectedRoute allowedRole="PATIENT">
                            <BookAppointment />
                        </ProtectedRoute>
                    }
                />

                {/* Doctor routes */}
                <Route
                    path="/doctor/dashboard"
                    element={
                        <ProtectedRoute allowedRole="DOCTOR">
                            <DoctorDashboard />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/doctor/appointments"
                    element={
                        <ProtectedRoute allowedRole="DOCTOR">
                            <DoctorAppointments />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/doctor/schedule"
                    element={
                        <ProtectedRoute allowedRole="DOCTOR">
                            <ManageSchedule />
                        </ProtectedRoute>
                    }
                />

                {/* Default redirect */}
                <Route path="/" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
