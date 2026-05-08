'use client';

import React, { useEffect, useState } from 'react';
import api from '@/lib/api';
import { Subject, User, AttendanceRecord } from '@/types';
import GlassCard from '@/components/GlassCard';
import toast from 'react-hot-toast';
import { Search, UserCheck, UserX } from 'lucide-react';

export default function FacultyDashboard() {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [students, setStudents] = useState<User[]>([]);
  const [selectedSubject, setSelectedSubject] = useState<number | ''>('');
  const [date, setDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [session, setSession] = useState<'MORNING' | 'AFTERNOON'>('MORNING');
  const [searchQuery, setSearchQuery] = useState('');
  
  const [attendanceRecords, setAttendanceRecords] = useState<Record<number, 'PRESENT' | 'ABSENT'>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const [subRes, stuRes] = await Promise.all([
          api.get('/subjects/my-subjects'),
          api.get('/users/students')
        ]);
        setSubjects(subRes.data);
        setStudents(stuRes.data);
      } catch (error) {
        toast.error('Failed to load dashboard data');
      }
    };
    fetchInitialData();
  }, []);

  const handleMarkAttendance = async (studentId: number, status: 'PRESENT' | 'ABSENT') => {
    if (!selectedSubject) {
      toast.error('Please select a subject first');
      return;
    }
    
    setLoading(true);
    try {
      await api.post('/attendance/mark', {
        studentId,
        subjectId: selectedSubject,
        date,
        session,
        status
      });
      setAttendanceRecords(prev => ({ ...prev, [studentId]: status }));
      toast.success('Attendance recorded');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to mark attendance');
    } finally {
      setLoading(false);
    }
  };

  const filteredStudents = students.filter(s => 
    s.fullName.toLowerCase().includes(searchQuery.toLowerCase()) || 
    s.username.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <header>
        <h1 className="text-3xl font-bold text-white mb-2">Faculty Portal</h1>
        <p className="text-gray-400">Manage class attendance efficiently.</p>
      </header>

      <GlassCard className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-2">Subject</label>
          <select 
            className="w-full glass-input"
            value={selectedSubject}
            onChange={e => setSelectedSubject(Number(e.target.value))}
          >
            <option value="" disabled className="text-black">Select a subject...</option>
            {subjects.map(s => (
              <option key={s.id} value={s.id} className="text-black">{s.code} - {s.name}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-300 mb-2">Date</label>
          <input 
            type="date" 
            className="w-full glass-input"
            value={date}
            onChange={e => setDate(e.target.value)}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-300 mb-2">Session</label>
          <select 
            className="w-full glass-input"
            value={session}
            onChange={e => setSession(e.target.value as any)}
          >
            <option value="MORNING" className="text-black">Morning (09:00 - 10:00)</option>
            <option value="AFTERNOON" className="text-black">Afternoon (13:30 - 14:30)</option>
          </select>
        </div>
      </GlassCard>

      <section>
        <div className="flex flex-col sm:flex-row justify-between items-center mb-6 space-y-4 sm:space-y-0">
          <h2 className="text-xl font-semibold text-gray-200">Student List</h2>
          <div className="relative w-full sm:w-64">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <input 
              type="text"
              placeholder="Search students..."
              className="w-full glass-input pl-10"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
            />
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          {filteredStudents.map(student => (
            <GlassCard key={student.id} className="flex justify-between items-center p-4 hover:bg-white/5">
              <div className="flex items-center space-x-4">
                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary to-secondary flex items-center justify-center text-white font-bold">
                  {student.fullName.charAt(0)}
                </div>
                <div>
                  <h3 className="text-white font-medium">{student.fullName}</h3>
                  <p className="text-sm text-gray-400">@{student.username}</p>
                </div>
              </div>

              <div className="flex space-x-2">
                <button
                  onClick={() => handleMarkAttendance(student.id, 'PRESENT')}
                  disabled={loading}
                  className={`p-2 rounded-lg transition-colors flex items-center space-x-1 ${
                    attendanceRecords[student.id] === 'PRESENT' 
                      ? 'bg-green-500 text-white' 
                      : 'bg-glass-bg border border-glass-border text-gray-400 hover:text-green-400'
                  }`}
                >
                  <UserCheck size={18} />
                  <span className="hidden sm:inline text-sm">Present</span>
                </button>
                <button
                  onClick={() => handleMarkAttendance(student.id, 'ABSENT')}
                  disabled={loading}
                  className={`p-2 rounded-lg transition-colors flex items-center space-x-1 ${
                    attendanceRecords[student.id] === 'ABSENT' 
                      ? 'bg-red-500 text-white' 
                      : 'bg-glass-bg border border-glass-border text-gray-400 hover:text-red-400'
                  }`}
                >
                  <UserX size={18} />
                  <span className="hidden sm:inline text-sm">Absent</span>
                </button>
              </div>
            </GlassCard>
          ))}
          {filteredStudents.length === 0 && (
            <div className="col-span-full text-center py-8 text-gray-500">
              No students found.
            </div>
          )}
        </div>
      </section>
    </div>
  );
}
