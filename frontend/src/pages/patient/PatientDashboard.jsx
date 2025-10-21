import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function PatientDashboard() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow-sm">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1 className="text-2xl font-bold text-primary-600">🏥 MediSync</h1>
                    <div className="flex items-center gap-4">
                        <span className="text-sm text-gray-600">{user?.email}</span>
                        <button onClick={handleLogout} className="btn-secondary text-sm">
                            Logout
                        </button>
                    </div>
                </div>
            </nav>

            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="mb-8">
                    <h2 className="text-3xl font-bold text-gray-900 mb-2">
                        Patient Dashboard
                    </h2>
                    <p className="text-gray-600">Welcome back, {user?.email}!</p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div className="card">
                        <div className="text-4xl mb-4">📅</div>
                        <h3 className="text-xl font-semibold mb-2">My Appointments</h3>
                        <p className="text-gray-600 mb-4">View and manage your appointments</p>
                        <button
                            onClick={() => navigate('/patient/appointments')}
                            className="btn-primary w-full text-sm"
                        >
                            View Appointments
                        </button>
                    </div>

                    <div className="card">
                        <div className="text-4xl mb-4">📋</div>
                        <h3 className="text-xl font-semibold mb-2">Medical Records</h3>
                        <p className="text-gray-600 mb-4">Access your medical history</p>
                        <button className="btn-primary w-full text-sm">
                            View Records
                        </button>
                    </div>

                    <div className="card">
                        <div className="text-4xl mb-4">🔍</div>
                        <h3 className="text-xl font-semibold mb-2">Book Appointment</h3>
                        <p className="text-gray-600 mb-4">Schedule a new appointment</p>
                        <button
                            onClick={() => navigate('/patient/book-appointment')}
                            className="btn-primary w-full text-sm"
                        >
                            Book Now
                        </button>
                    </div>
                </div>

                <div className="mt-8 card">
                    <h3 className="text-xl font-semibold mb-4">✅ Authentication Working!</h3>
                    <p className="text-gray-600">
                        You successfully logged in as a patient. We'll build out the features next!
                    </p>
                </div>
            </div>
        </div>
    );
}
