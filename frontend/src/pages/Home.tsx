import React from 'react';
import { Link } from 'react-router-dom';
import { authService } from '../services/authService';
import './Home.css';

const Home: React.FC = () => {
  const user = authService.getCurrentUser();

  return (
    <div className="home-container">
      <div className="hero-section">
        <h1>Welcome to MediSync</h1>
        <p className="hero-subtitle">
          Your complete healthcare appointment management system
        </p>
        
        {user ? (
          <div className="welcome-message">
            <h2>Hello, {user.firstName}!</h2>
            <Link 
              to={user.role === 'DOCTOR' ? '/doctor/dashboard' : '/patient/dashboard'}
              className="cta-button"
            >
              Go to Dashboard
            </Link>
          </div>
        ) : (
          <div className="cta-section">
            <Link to="/register" className="cta-button primary">
              Get Started
            </Link>
            <Link to="/login" className="cta-button secondary">
              Login
            </Link>
          </div>
        )}
      </div>

      <div className="features-section">
        <h2>Features</h2>
        <div className="features-grid">
          <div className="feature-card">
            <h3>For Doctors</h3>
            <p>Manage your schedule, appointments, and patient records</p>
          </div>
          <div className="feature-card">
            <h3>For Patients</h3>
            <p>Book appointments with doctors and manage your health records</p>
          </div>
          <div className="feature-card">
            <h3>Smart Scheduling</h3>
            <p>Intelligent time slot management and availability tracking</p>
          </div>
          <div className="feature-card">
            <h3>🔒 Secure</h3>
            <p>Enterprise-grade security for your medical data</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
