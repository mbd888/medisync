import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { scheduleApi } from '../../api/scheduleApi';

export default function ManageSchedule() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [schedules, setSchedules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [formData, setFormData] = useState({
        dayOfWeek: 'MONDAY',
        startTime: '09:00',
        endTime: '17:00',
        slotDuration: 30,
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchSchedules();
    }, []);

    const fetchSchedules = async () => {
        try {
            setLoading(true);
            const data = await scheduleApi.getMySchedule();
            setSchedules(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            await scheduleApi.createSchedule(formData);
            setSuccess('Schedule created successfully!');
            setShowForm(false);
            fetchSchedules();
            setFormData({
                dayOfWeek: 'MONDAY',
                startTime: '09:00',
                endTime: '17:00',
                slotDuration: 30,
            });
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create schedule');
        }
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure you want to delete this schedule?')) {
            return;
        }

        try {
            await scheduleApi.deleteSchedule(id);
            setSuccess('Schedule deleted successfully!');
            fetchSchedules();
        } catch (err) {
            setError('Failed to delete schedule');
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navbar */}
            <nav className="bg-white shadow-sm">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1
                        className="text-2xl font-bold text-primary-600 cursor-pointer"
                        onClick={() => navigate('/doctor/dashboard')}
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

            <div className="max-w-4xl mx-auto px-4 py-8">
                <div className="mb-8 flex justify-between items-center">
                    <div>
                        <h2 className="text-3xl font-bold text-gray-900 mb-2">
                            Manage Schedule
                        </h2>
                        <p className="text-gray-600">Set your working hours</p>
                    </div>
                    <button
                        onClick={() => navigate('/doctor/dashboard')}
                        className="btn-secondary"
                    >
                        ← Back to Dashboard
                    </button>
                </div>

                {success && (
                    <div className="card bg-green-50 text-green-800 mb-6">
                        {success}
                    </div>
                )}

                {error && (
                    <div className="card bg-red-50 text-red-700 mb-6">
                        {error}
                    </div>
                )}

                {/* Add Schedule Button */}
                {!showForm && (
                    <button
                        onClick={() => setShowForm(true)}
                        className="btn-primary mb-6"
                    >
                        + Add Working Hours
                    </button>
                )}

                {/* Add Schedule Form */}
                {showForm && (
                    <div className="card mb-6">
                        <h3 className="text-xl font-semibold mb-4">Add Working Hours</h3>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium mb-2">Day of Week</label>
                                <select
                                    value={formData.dayOfWeek}
                                    onChange={(e) => setFormData({ ...formData, dayOfWeek: e.target.value })}
                                    className="input-field"
                                    required
                                >
                                    {daysOfWeek.map(day => (
                                        <option key={day} value={day}>
                                            {day.charAt(0) + day.slice(1).toLowerCase()}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium mb-2">Start Time</label>
                                    <input
                                        type="time"
                                        value={formData.startTime}
                                        onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                                        className="input-field"
                                        required
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium mb-2">End Time</label>
                                    <input
                                        type="time"
                                        value={formData.endTime}
                                        onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                                        className="input-field"
                                        required
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-2">Slot Duration (minutes)</label>
                                <input
                                    type="number"
                                    value={formData.slotDuration}
                                    onChange={(e) => setFormData({ ...formData, slotDuration: parseInt(e.target.value) })}
                                    className="input-field"
                                    min="15"
                                    max="120"
                                    step="15"
                                    required
                                />
                            </div>

                            <div className="flex gap-3">
                                <button type="submit" className="btn-primary">
                                    Create Schedule
                                </button>
                                <button
                                    type="button"
                                    onClick={() => setShowForm(false)}
                                    className="btn-secondary"
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                )}

                {/* Current Schedules */}
                <div className="card">
                    <h3 className="text-xl font-semibold mb-4">Current Schedule</h3>

                    {loading && <p className="text-gray-600">Loading schedules...</p>}

                    {!loading && schedules.length === 0 && (
                        <p className="text-gray-600">No schedule set yet. Add your working hours above.</p>
                    )}

                    {!loading && schedules.length > 0 && (
                        <div className="space-y-3">
                            {schedules.map((schedule) => (
                                <div key={schedule.id} className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
                                    <div>
                                        <p className="font-semibold">
                                            {schedule.dayOfWeek.charAt(0) + schedule.dayOfWeek.slice(1).toLowerCase()}
                                        </p>
                                        <p className="text-sm text-gray-600">
                                            {new Date('2000-01-01T' + schedule.startTime).toLocaleTimeString('en-US', {
                                                hour: 'numeric',
                                                minute: '2-digit',
                                                hour12: true
                                            })} - {new Date('2000-01-01T' + schedule.endTime).toLocaleTimeString('en-US', {
                                            hour: 'numeric',
                                            minute: '2-digit',
                                            hour12: true
                                        })}
                                        </p>
                                        <p className="text-xs text-gray-500">
                                            {schedule.slotDuration} minute slots
                                        </p>
                                    </div>
                                    <button
                                        onClick={() => handleDelete(schedule.id)}
                                        className="text-red-600 hover:bg-red-50 px-3 py-1 rounded transition-colors text-sm"
                                    >
                                        Delete
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
