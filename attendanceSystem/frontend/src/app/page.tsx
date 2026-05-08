'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import toast, { Toaster } from 'react-hot-toast';
import GlassCard from '@/components/GlassCard';

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const response = await api.post('/auth/login', { username, password });
      const data = response.data;
      
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify({
        id: data.userId,
        username: data.username,
        fullName: data.fullName,
        role: data.role
      }));
      
      toast.success(`Welcome, ${data.fullName}!`);
      
      if (data.role === 'FACULTY' || data.role === 'ADMIN') {
        router.push('/dashboard/faculty');
      } else {
        router.push('/dashboard/student');
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-4 relative overflow-hidden">
      <Toaster position="top-right" toastOptions={{ className: 'glass text-white' }} />
      
      {/* Floating decorative elements */}
      <div className="absolute top-1/4 left-1/4 w-64 h-64 bg-primary/20 rounded-full blur-3xl mix-blend-screen animate-float"></div>
      <div className="absolute bottom-1/4 right-1/4 w-80 h-80 bg-secondary/20 rounded-full blur-3xl mix-blend-screen animate-float" style={{ animationDelay: '2s' }}></div>

      <div className="z-10 w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-extrabold mb-2 bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            Antigravity
          </h1>
          <p className="text-gray-400">Attendance Management System</p>
        </div>

        <GlassCard className="p-8">
          <form onSubmit={handleLogin} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Username</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full glass-input"
                placeholder="Enter username"
                required
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full glass-input"
                placeholder="Enter password"
                required
              />
            </div>
            
            <button
              type="submit"
              disabled={loading}
              className="w-full glass-button mt-4"
            >
              {loading ? 'Authenticating...' : 'Sign In'}
            </button>
          </form>
          
          <div className="mt-6 text-xs text-gray-500 text-center space-y-1">
            <p>Demo accounts:</p>
            <p>Admin: admin / admin123</p>
            <p>Faculty: prof.smith / faculty123</p>
            <p>Student: john.doe / student123</p>
          </div>
        </GlassCard>
      </div>
    </main>
  );
}
