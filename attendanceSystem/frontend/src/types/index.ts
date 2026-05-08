export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: 'STUDENT' | 'FACULTY' | 'ADMIN';
}

export interface Subject {
  id: number;
  code: string;
  name: string;
  facultyName: string;
  facultyId: number;
}

export interface AttendanceRecord {
  id: number;
  studentId: number;
  studentName: string;
  studentUsername: string;
  subjectId: number;
  subjectCode: string;
  subjectName: string;
  date: string;
  session: 'MORNING' | 'AFTERNOON';
  status: 'PRESENT' | 'ABSENT';
  markedByName: string;
}

export interface SubjectAttendanceSummary {
  subjectId: number;
  subjectCode: string;
  subjectName: string;
  totalClasses: number;
  presentCount: number;
  absentCount: number;
  percentage: number;
}
