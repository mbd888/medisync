import React, { useState } from 'react';
import api from '../services/api';
import type {AvailableSlot} from '../types';
import './Dashboard.css';

const PatientDashboard: React.FC = () => {
  const [doctorId, setDoctorId] = useState('');
  const [date, setDate] = useState('');
  const [slots, setSlots] = useState<AvailableSlot[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.get<AvailableSlot[]>(
        `/doctors/${doctorId}/available-slots?date=${date}`
      );
      setSlots(response.data);
    } catch (err: any) {
      setError('Failed to load available slots');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-container">
      <h1>Patient Dashboard</h1>

      {error && <div className="error-message">{error}</div>}

      <div className="form-card">
        <h3>Check Doctor Availability</h3>
        <form onSubmit={handleSearch}>
          <div className="form-group">
            <label>Doctor ID</label>
            <input
              type="number"
              value={doctorId}
              onChange={(e) => setDoctorId(e.target.value)}
              placeholder="Enter doctor ID"
              required
            />
          </div>

          <div className="form-group">
            <label>Date</label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              min={new Date().toISOString().split('T')[0]}
              required
            />
          </div>

          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? 'Searching...' : 'Search Availability'}
          </button>
        </form>
      </div>

      {slots.length > 0 && (
        <div className="slots-container">
          <h3>Available Time Slots</h3>
          <div className="slots-grid">
            {slots.map((slot, index) => (
              <div 
                key={index} 
                className={`slot-card ${slot.isAvailable ? 'available' : 'unavailable'}`}
              >
                <p className="slot-time">{slot.startTime} - {slot.endTime}</p>
                <span className="slot-status">
                  {slot.isAvailable ? '✓ Available' : '✗ Booked'}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default PatientDashboard;
