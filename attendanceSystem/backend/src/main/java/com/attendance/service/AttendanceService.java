package com.attendance.service;

import com.attendance.dto.*;
import com.attendance.entity.*;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    @Value("${app.session.bypass-time-check:false}")
    private boolean bypassTimeCheck;

    @Transactional
    public AttendanceRecordDto markAttendance(MarkAttendanceRequest req, String markedByUsername) {
        User student = userRepository.findById(Objects.requireNonNull(req.getStudentId(), "studentId must not be null"))
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + req.getStudentId()));
        Subject subject = subjectRepository.findById(Objects.requireNonNull(req.getSubjectId(), "subjectId must not be null"))
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + req.getSubjectId()));
        User faculty = userRepository.findByUsername(markedByUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + markedByUsername));

        // ── Session time-window enforcement ──────────────────────────────
        if (!bypassTimeCheck && !req.getSession().isOpen()) {
            throw new IllegalStateException(
                    "Cannot mark attendance: the " + req.getSession().getDisplayName() +
                    " session is currently closed. Attendance can only be marked during the session window."
            );
        }

        // ── Duplicate check ───────────────────────────────────────────────
        if (attendanceRepository.existsByStudentAndSubjectAndDateAndSession(
                student, subject, req.getDate(), req.getSession())) {
            // Update existing record instead of throwing
            Attendance existing = attendanceRepository
                    .findByStudentAndSubjectAndDateAndSession(student, subject, req.getDate(), req.getSession())
                    .get();
            existing.setStatus(req.getStatus());
            existing.setMarkedBy(faculty);
            attendanceRepository.save(existing);
            return toDto(existing);
        }

        Attendance attendance = Attendance.builder()
                .student(student)
                .subject(subject)
                .date(req.getDate())
                .session(req.getSession())
                .status(req.getStatus())
                .markedBy(faculty)
                .build();
        attendanceRepository.save(attendance);
        return toDto(attendance);
    }

    public List<AttendanceRecordDto> getStudentReport(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + username));
        return attendanceRepository.findByStudent(student)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SubjectAttendanceSummary> getStudentSummary(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + username));
        List<Subject> subjects = subjectRepository.findAll();
        return subjects.stream().map(subject -> {
            long total = attendanceRepository.countTotalByStudentAndSubject(student, subject);
            long present = attendanceRepository.countPresentByStudentAndSubject(student, subject);
            long absent = total - present;
            double percentage = total == 0 ? 0.0 : (present * 100.0 / total);
            return SubjectAttendanceSummary.builder()
                    .subjectId(subject.getId())
                    .subjectCode(subject.getCode())
                    .subjectName(subject.getName())
                    .totalClasses(total)
                    .presentCount(present)
                    .absentCount(absent)
                    .percentage(Math.round(percentage * 10.0) / 10.0)
                    .build();
        }).filter(s -> s.getTotalClasses() > 0).collect(Collectors.toList());
    }

    public List<AttendanceRecordDto> getAttendanceBySubjectAndDate(Long subjectId, String date) {
        Subject subject = subjectRepository.findById(Objects.requireNonNull(subjectId, "subjectId must not be null"))
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + subjectId));
        java.time.LocalDate localDate = java.time.LocalDate.parse(date);
        return attendanceRepository.findBySubjectAndDate(subject, localDate)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private AttendanceRecordDto toDto(Attendance a) {
        return AttendanceRecordDto.builder()
                .id(a.getId())
                .studentId(a.getStudent().getId())
                .studentName(a.getStudent().getFullName())
                .studentUsername(a.getStudent().getUsername())
                .subjectId(a.getSubject().getId())
                .subjectCode(a.getSubject().getCode())
                .subjectName(a.getSubject().getName())
                .date(a.getDate().toString())
                .session(a.getSession().name())
                .status(a.getStatus().name())
                .markedByName(a.getMarkedBy().getFullName())
                .build();
    }
}
