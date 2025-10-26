import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { appointmentApi } from '../../api/appointmentApi';
import { format, parse } from 'date-fns';

export default function MyAppointments() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchAppointments().catch(console.error);
    }, []);

    const fetchAppointments = async () => {
        try {
            setLoading(true);
            const data = await appointmentApi.getMyAppointments();
            setAppointments(data);
            setError('');
        } catch (err) {
            setError('Failed to load appointments');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = async (id) => {
        if (!confirm('Are you sure you want to cancel this appointment?')) {
            return;
        }

        try {
            await appointmentApi.cancelAppointment(id);
            await fetchAppointments(); // Refresh list
        } catch (err) {
            alert('Failed to cancel appointment');
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const formatTime12Hour = (timeString) => {
        if (!timeString) return '';

        // Parse the time string (assumes format like "14:30:00" or "14:30")
        const parsedTime = parse(timeString, timeString.includes(':')
        && timeString.split(':').length === 3 ? 'HH:mm:ss' : 'HH:mm', new Date());

        // Format to 12-hour time with AM/PM
        return format(parsedTime, 'h:mm a');
    };

    const getStatusBadge = (status) => {
        const styles = {
            SCHEDULED: 'bg-blue-100 text-blue-800',
            COMPLETED: 'bg-green-100 text-green-800',
            CANCELLED: 'bg-red-100 text-red-800',
            NO_SHOW: 'bg-gray-100 text-gray-800',
        };
        return styles[status] || 'bg-gray-100 text-gray-800';
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navbar */}
            <nav className="bg-white shadow-sm">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1
                        className="text-2xl font-bold text-primary-600 cursor-pointer"
                        onClick={() => navigate('/patient/dashboard')}
                    >
                        MediSync
                    </h1>
                    <div className="flex items-center gap-4">
                        <span className="text-sm text-gray-600">{user?.email}</span>
                        <button onClick={handleLogout} className="btn-secondary text-sm">
                            Logout
                        </button>
                    </div>
                </div>
            </nav>

            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="mb-8 flex justify-between items-center">
                    <div>
                        <h2 className="text-3xl font-bold text-gray-900 mb-2">
                            My Appointments
                        </h2>
                        <p className="text-gray-600">View and manage your appointments</p>
                    </div>
                    <button
                        onClick={() => navigate('/patient/dashboard')}
                        className="btn-secondary"
                    >
                        ← Back to Dashboard
                    </button>
                </div>

                {loading && (
                    <div className="text-center py-12">
                        <div className="text-gray-600">Loading appointments...</div>
                    </div>
                )}

                {error && (
                    <div className="card bg-red-50 text-red-700 mb-6">
                        {error}
                    </div>
                )}

                {!loading && appointments.length === 0 && (
                    <div className="card text-center py-12">
                        <div className="text-6xl mb-4">📅</div>
                        <h3 className="text-xl font-semibold mb-2">No appointments yet</h3>
                        <p className="text-gray-600 mb-6">Book your first appointment to get started</p>
                        <button className="btn-primary">
                            Book Appointment
                        </button>
                    </div>
                )}

                {!loading && appointments.length > 0 && (
                    <div className="space-y-4">
                        {appointments.map((appointment) => (
                            <div key={appointment.id} className="card">
                                <div className="flex justify-between items-start">
                                    <div className="flex-1">
                                        <div className="flex items-center gap-3 mb-2">
                                            <h3 className="text-xl font-semibold">
                                                {appointment.doctorName || 'Doctor'}
                                            </h3>
                                            <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusBadge(appointment.status)}`}>
                                                {appointment.status}
                                            </span>
                                        </div>

                                        {appointment.doctorSpecialization && (
                                            <p className="text-gray-600 mb-3">
                                                {appointment.doctorSpecialization}
                                            </p>
                                        )}

                                        <div className="space-y-2 text-sm text-gray-700">
                                            <div className="flex items-center gap-2">
                                                <span className="font-medium">Date:</span>
                                                <span>
                                                  {appointment.appointmentDate
                                                      ? new Date(appointment.appointmentDate + 'T12:00:00')
                                                          .toLocaleDateString('en-US',
                                                              { month: 'long', day: 'numeric', year: 'numeric' })
                                                      : 'N/A'}
                                                </span>
                                            </div>
                                            <div className="flex items-center gap-2">
                                                <span className="font-medium">Time:</span>
                                                <span>{formatTime12Hour(appointment.startTime)} - {formatTime12Hour(appointment.endTime)}</span>
                                            </div>
                                            {appointment.reason && (
                                                <div className="flex items-center gap-2">
                                                    <span className="font-medium">Reason:</span>
                                                    <span>{appointment.reason}</span>
                                                </div>
                                            )}
                                        </div>
                                    </div>

                                    {appointment.status === 'SCHEDULED' && (
                                        <button
                                            onClick={() => handleCancel(appointment.id)}
                                            className="ml-4 px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors text-sm font-medium"
                                        >
                                            Cancel
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
