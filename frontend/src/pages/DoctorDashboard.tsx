import React, { useState, useEffect } from 'react';
import api from '../services/api';
import type {DoctorSchedule, CreateScheduleRequest} from '../types';
import './Dashboard.css';

const DoctorDashboard: React.FC = () => {
  const [schedules, setSchedules] = useState<DoctorSchedule[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState<CreateScheduleRequest>({
    dayOfWeek: 'MONDAY',
    startTime: '09:00',
    endTime: '17:00',
    slotDuration: 30,
  });

  useEffect(() => {
    fetchSchedules();
  }, []);

  const fetchSchedules = async () => {
    try {
      setLoading(true);
      const response = await api.get<DoctorSchedule[]>('/doctors/schedule');
      setSchedules(response.data);
      setError('');
    } catch (err: any) {
      setError('Failed to load schedules');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/doctors/schedule', formData);
      setShowForm(false);
      fetchSchedules();
      setFormData({
        dayOfWeek: 'MONDAY',
        startTime: '09:00',
        endTime: '17:00',
        slotDuration: 30,
      });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create schedule');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this schedule?')) return;
    
    try {
      await api.delete(`/doctors/schedule/${id}`);
      fetchSchedules();
    } catch (err: any) {
      setError('Failed to delete schedule');
    }
  };

  if (loading) return <div className="dashboard-container">Loading...</div>;

  return (
    <div className="dashboard-container">
      <h1>Doctor Dashboard</h1>
      
      {error && <div className="error-message">{error}</div>}

      <div className="dashboard-header">
        <h2>My Schedules</h2>
        <button onClick={() => setShowForm(!showForm)} className="add-button">
          {showForm ? 'Cancel' : '+ Add Schedule'}
        </button>
      </div>

      {showForm && (
        <div className="form-card">
          <h3>Create New Schedule</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Day of Week</label>
              <select
                value={formData.dayOfWeek}
                onChange={(e) => setFormData({ ...formData, dayOfWeek: e.target.value })}
                required
              >
                <option value="MONDAY">Monday</option>
                <option value="TUESDAY">Tuesday</option>
                <option value="WEDNESDAY">Wednesday</option>
                <option value="THURSDAY">Thursday</option>
                <option value="FRIDAY">Friday</option>
                <option value="SATURDAY">Saturday</option>
                <option value="SUNDAY">Sunday</option>
              </select>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Start Time</label>
                <input
                  type="time"
                  value={formData.startTime}
                  onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>End Time</label>
                <input
                  type="time"
                  value={formData.endTime}
                  onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Slot Duration (minutes)</label>
              <input
                type="number"
                value={formData.slotDuration}
                onChange={(e) => setFormData({ ...formData, slotDuration: parseInt(e.target.value) })}
                min="15"
                step="15"
                required
              />
            </div>

            <button type="submit" className="submit-button">Create Schedule</button>
          </form>
        </div>
      )}

      <div className="schedules-grid">
        {schedules.length === 0 ? (
          <p className="empty-state">No schedules yet. Create your first schedule to get started!</p>
        ) : (
          schedules.map((schedule) => (
            <div key={schedule.id} className="schedule-card">
              <div className="schedule-header">
                <h3>{schedule.dayOfWeek}</h3>
                <span className={`status ${schedule.isAvailable ? 'available' : 'unavailable'}`}>
                  {schedule.isAvailable ? 'Available' : 'Unavailable'}
                </span>
              </div>
              <div className="schedule-details">
                <p><strong>Time:</strong> {schedule.startTime} - {schedule.endTime}</p>
                <p><strong>Slot Duration:</strong> {schedule.slotDuration} minutes</p>
              </div>
              <button onClick={() => handleDelete(schedule.id)} className="delete-button">
                Delete
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default DoctorDashboard;
