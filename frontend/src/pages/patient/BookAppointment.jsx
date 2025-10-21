import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { appointmentApi } from '../../api/appointmentApi';
import { profileApi } from '../../api/profileApi';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

export default function BookAppointment() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [doctors, setDoctors] = useState([]);
    const [doctorSchedule, setDoctorSchedule] = useState([]);
    const [formData, setFormData] = useState({
        doctorId: '',
        appointmentDate: '',
        startTime: '',
        reason: '',
    });
    const [availableSlots, setAvailableSlots] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingDoctors, setLoadingDoctors] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        fetchDoctors();
    }, []);

    const fetchDoctors = async () => {
        try {
            setLoadingDoctors(true);
            const data = await profileApi.getAllDoctors();
            setDoctors(data);
        } catch (err) {
            setError('Failed to load doctors');
        } finally {
            setLoadingDoctors(false);
        }
    };

    const fetchDoctorSchedule = async (doctorId) => {
        try {
            const schedule = await profileApi.getDoctorSchedule(doctorId);
            setDoctorSchedule(schedule);
        } catch (err) {
            setDoctorSchedule([]);
        }
    };

    const handleDoctorChange = (doctorId) => {
        setFormData({
            ...formData,
            doctorId,
            appointmentDate: '',
            startTime: ''
        });
        setAvailableSlots([]);
        if (doctorId) {
            fetchDoctorSchedule(doctorId);
        } else {
            setDoctorSchedule([]);
        }
    };

    const isDateAllowed = (dateString) => {
        if (!doctorSchedule.length) return false;
        const date = new Date(dateString + 'T12:00:00');
        const dayName = date.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
        return doctorSchedule.some(s => s.dayOfWeek === dayName && s.isAvailable);
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const handleCheckAvailability = async () => {
        if (!formData.doctorId || !formData.appointmentDate) {
            setError('Please enter Doctor ID and select a date');
            return;
        }

        try {
            setLoading(true);
            setError('');
            const slots = await appointmentApi.getAvailableSlots(
                formData.doctorId,
                formData.appointmentDate
            );
            setAvailableSlots(slots);
        } catch (err) {
            setError('Failed to load available slots. Check if doctor exists and has a schedule.');
            setAvailableSlots([]);
        } finally {
            setLoading(false);
        }
    };

    const handleBookAppointment = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess(false);

        if (!formData.startTime) {
            setError('Please select a time slot');
            return;
        }

        try {
            setLoading(true);
            await appointmentApi.bookAppointment({
                doctorId: parseInt(formData.doctorId),
                appointmentDate: formData.appointmentDate,
                startTime: formData.startTime,
                reason: formData.reason,
            });

            setSuccess(true);
            setTimeout(() => {
                navigate('/patient/appointments');
            }, 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to book appointment');
        } finally {
            setLoading(false);
        }
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
                        🏥 MediSync
                    </h1>
                    <div className="flex items-center gap-4">
                        <span className="text-sm text-gray-600">{user?.email}</span>
                        <button onClick={handleLogout} className="btn-secondary text-sm">
                            Logout
                        </button>
                    </div>
                </div>
            </nav>

            <div className="max-w-4xl mx-auto px-4 py-8">
                <div className="mb-8 flex justify-between items-center">
                    <div>
                        <h2 className="text-3xl font-bold text-gray-900 mb-2">
                            Book Appointment
                        </h2>
                        <p className="text-gray-600">Schedule your next visit</p>
                    </div>
                    <button
                        onClick={() => navigate('/patient/dashboard')}
                        className="btn-secondary"
                    >
                        ← Back to Dashboard
                    </button>
                </div>

                {success && (
                    <div className="card bg-green-50 text-green-800 mb-6">
                        ✅ Appointment booked successfully! Redirecting...
                    </div>
                )}

                {error && (
                    <div className="card bg-red-50 text-red-700 mb-6">
                        {error}
                    </div>
                )}

                <form onSubmit={handleBookAppointment} className="space-y-6">
                    {/* Doctor Selection */}
                    <div className="card">
                        <h3 className="text-xl font-semibold mb-4">Select Doctor</h3>
                        <div className="space-y-4">
                            {loadingDoctors ? (
                                <p className="text-gray-600">Loading doctors...</p>
                            ) : doctors.length === 0 ? (
                                <p className="text-gray-600">No doctors available</p>
                            ) : (
                                <div>
                                    <label className="block text-sm font-medium mb-2">
                                        Choose a Doctor <span className="text-red-500">*</span>
                                    </label>
                                    <select
                                        value={formData.doctorId}
                                        onChange={(e) => handleDoctorChange(e.target.value)}
                                        className="input-field"
                                        required
                                    >
                                        <option value="">Select a doctor...</option>
                                        {doctors.map((doctor) => (
                                            <option key={doctor.id} value={doctor.id}>
                                                Dr. {doctor.firstName} {doctor.lastName} - {doctor.specialization || 'General'}
                                            </option>
                                        ))}
                                    </select>

                                    {formData.doctorId && (
                                        <div className="mt-4 p-4 bg-primary-50 rounded-lg">
                                            {(() => {
                                                const selectedDoctor = doctors.find(d => d.id === parseInt(formData.doctorId));
                                                return selectedDoctor ? (
                                                    <div>
                                                        <p className="font-semibold text-primary-900">
                                                            Dr. {selectedDoctor.firstName} {selectedDoctor.lastName}
                                                        </p>
                                                        <p className="text-sm text-primary-700">
                                                            {selectedDoctor.specialization || 'General Practice'}
                                                        </p>
                                                        {selectedDoctor.qualification && (
                                                            <p className="text-sm text-primary-600 mt-1">
                                                                {selectedDoctor.qualification}
                                                            </p>
                                                        )}
                                                        {selectedDoctor.yearsOfExperience && (
                                                            <p className="text-sm text-primary-600">
                                                                {selectedDoctor.yearsOfExperience} years of experience
                                                            </p>
                                                        )}
                                                    </div>
                                                ) : null;
                                            })()}
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Date Selection */}
                    <div className="card">
                        <h3 className="text-xl font-semibold mb-4">Appointment Date</h3>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium mb-2">
                                    Date <span className="text-red-500">*</span>
                                </label>
                                <DatePicker
                                    selected={formData.appointmentDate ? new Date(formData.appointmentDate + 'T12:00:00') : null}
                                    onChange={(date) => {
                                        if (date) {
                                            const dateString = date.toISOString().split('T')[0];
                                            setFormData({ ...formData, appointmentDate: dateString, startTime: '' });
                                            setAvailableSlots([]);
                                        }
                                    }}
                                    minDate={new Date()}
                                    filterDate={(date) => {
                                        const dayName = date.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
                                        return doctorSchedule.some(s => s.dayOfWeek === dayName && s.isAvailable);
                                    }}
                                    disabled={!formData.doctorId || doctorSchedule.length === 0}
                                    placeholderText="Select a date"
                                    className="input-field w-full"
                                    dateFormat="MMMM d, yyyy"
                                />
                                {formData.doctorId && doctorSchedule.length === 0 && (
                                    <p className="text-sm text-red-600 mt-2">
                                        ⚠️ This doctor has no schedule set yet
                                    </p>
                                )}
                            </div>

                            <button
                                type="button"
                                onClick={handleCheckAvailability}
                                disabled={loading || !formData.doctorId || !formData.appointmentDate}
                                className="btn-primary disabled:opacity-50"
                            >
                                {loading ? 'Checking...' : 'Check Available Time Slots'}
                            </button>
                        </div>
                    </div>

                    {/* Available Slots */}
                    {availableSlots.length > 0 && (
                        <div className="card">
                            <h3 className="text-xl font-semibold mb-4">Available Time Slots</h3>
                            <div className="grid grid-cols-4 gap-3">
                                {availableSlots
                                    .filter(slot => slot.isAvailable)
                                    .map((slot, index) => (
                                        <button
                                            key={index}
                                            type="button"
                                            onClick={() => setFormData({ ...formData, startTime: slot.startTime })}
                                            className={`p-3 rounded-lg border-2 transition-all ${
                                                formData.startTime === slot.startTime
                                                    ? 'border-primary-500 bg-primary-50 text-primary-700 font-semibold'
                                                    : 'border-gray-200 hover:border-primary-300'
                                            }`}
                                        >
                                            {slot.startTime}
                                        </button>
                                    ))}
                            </div>
                            {availableSlots.filter(slot => slot.isAvailable).length === 0 && (
                                <p className="text-gray-600 text-center py-4">
                                    No available slots for this date. Try another day.
                                </p>
                            )}
                        </div>
                    )}

                    {/* Reason for Visit */}
                    <div className="card">
                        <h3 className="text-xl font-semibold mb-4">Reason for Visit</h3>
                        <textarea
                            value={formData.reason}
                            onChange={(e) => setFormData({ ...formData, reason: e.target.value })}
                            className="input-field"
                            rows="4"
                            placeholder="Describe your symptoms or reason for visit..."
                        />
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        disabled={loading || !formData.startTime}
                        className="btn-primary w-full text-lg py-3 disabled:opacity-50"
                    >
                        {loading ? 'Booking...' : 'Book Appointment'}
                    </button>
                </form>
            </div>
        </div>
    );
}
