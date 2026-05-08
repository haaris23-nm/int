'use client';

import React, { useEffect, useState } from 'react';
import api from '@/lib/api';
import { SubjectAttendanceSummary, AttendanceRecord } from '@/types';
import CircularProgress from '@/components/CircularProgress';
import GlassCard from '@/components/GlassCard';
import { Calendar, CheckCircle, XCircle } from 'lucide-react';
import toast from 'react-hot-toast';

export default function StudentDashboard() {
  const [summaries, setSummaries] = useState<SubjectAttendanceSummary[]>([]);
  const [recentRecords, setRecentRecords] = useState<AttendanceRecord[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [summaryRes, reportRes] = await Promise.all([
          api.get('/attendance/my-summary'),
          api.get('/attendance/my-report')
        ]);
        setSummaries(summaryRes.data);
        
        // Sort records by date descending and take top 5
        const sortedRecords = reportRes.data.sort((a: any, b: any) => 
          new Date(b.date).getTime() - new Date(a.date).getTime()
        ).slice(0, 5);
        setRecentRecords(sortedRecords);
        
      } catch (error) {
        toast.error('Failed to load attendance data');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return <div className="flex justify-center mt-20"><div className="animate-pulse-slow text-primary text-xl">Loading your space...</div></div>;

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <header>
        <h1 className="text-3xl font-bold text-white mb-2">My Attendance</h1>
        <p className="text-gray-400">Keep track of your academic presence.</p>
      </header>

      <section>
        <h2 className="text-xl font-semibold text-gray-200 mb-4">Subject Overview</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {summaries.map(summary => (
            <GlassCard key={summary.subjectId} className="flex flex-col items-center hover:scale-105 transition-transform">
              <CircularProgress 
                percentage={summary.percentage} 
                color={summary.percentage >= 75 ? '#00d4ff' : summary.percentage >= 60 ? '#facc15' : '#ef4444'}
                label={summary.subjectCode}
              />
              <div className="mt-4 text-center">
                <h3 className="font-medium text-white truncate w-32" title={summary.subjectName}>{summary.subjectName}</h3>
                <p className="text-xs text-gray-400 mt-1">
                  {summary.presentCount} / {summary.totalClasses} Classes
                </p>
              </div>
            </GlassCard>
          ))}
          {summaries.length === 0 && <p className="text-gray-500">No attendance data found.</p>}
        </div>
      </section>

      <section>
        <h2 className="text-xl font-semibold text-gray-200 mb-4">Recent Activity</h2>
        <GlassCard className="overflow-hidden p-0">
          <div className="divide-y divide-glass-border">
            {recentRecords.map(record => (
              <div key={record.id} className="p-4 flex items-center justify-between hover:bg-white/5 transition-colors">
                <div className="flex items-center space-x-4">
                  <div className={`p-2 rounded-full ${record.status === 'PRESENT' ? 'bg-green-500/20 text-green-400' : 'bg-red-500/20 text-red-400'}`}>
                    {record.status === 'PRESENT' ? <CheckCircle size={20} /> : <XCircle size={20} />}
                  </div>
                  <div>
                    <p className="font-medium text-white">{record.subjectCode} - {record.subjectName}</p>
                    <p className="text-sm text-gray-400 flex items-center space-x-1">
                      <Calendar size={14} />
                      <span>{record.date} • {record.session}</span>
                    </p>
                  </div>
                </div>
                <div className={`text-sm font-bold ${record.status === 'PRESENT' ? 'text-green-400' : 'text-red-400'}`}>
                  {record.status}
                </div>
              </div>
            ))}
            {recentRecords.length === 0 && <div className="p-6 text-center text-gray-500">No recent records.</div>}
          </div>
        </GlassCard>
      </section>
    </div>
  );
}
