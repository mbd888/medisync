import { useState } from 'react';
import { useAuth } from './context/AuthContext';

function App() {
    const { login, user, logout } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setMessage('Logging in...');

        const result = await login(email, password);

        if (result.success) {
            setMessage('✅ Login successful!');
        } else {
            setMessage('❌ ' + result.error);
        }
    };

    if (user) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-100">
                <div className="card max-w-md w-full">
                    <h1 className="text-3xl font-bold text-primary-600 mb-4">
                        🏥 MediSync
                    </h1>
                    <div className="mb-4 p-4 bg-green-100 text-green-800 rounded-lg">
                        <p className="font-semibold">Logged in as:</p>
                        <p>{user.email}</p>
                        <p className="text-sm">Role: {user.role}</p>
                    </div>
                    <button onClick={logout} className="btn-secondary w-full">
                        Logout
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="card max-w-md w-full">
                <h1 className="text-3xl font-bold text-primary-600 mb-4">
                    🏥 MediSync
                </h1>
                <p className="text-gray-600 mb-6">
                    Test Login (Use existing account)
                </p>

                <form onSubmit={handleLogin} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-2">Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="input-field"
                            placeholder="patient1@test.com"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-2">Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="input-field"
                            placeholder="password123"
                            required
                        />
                    </div>

                    {message && (
                        <div className="p-3 bg-gray-100 rounded-lg text-sm">
                            {message}
                        </div>
                    )}

                    <button type="submit" className="btn-primary w-full">
                        Test Login
                    </button>
                </form>
            </div>
        </div>
    );
}

export default App;
