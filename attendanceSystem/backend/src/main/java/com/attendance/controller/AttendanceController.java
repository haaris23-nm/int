package com.attendance.controller;

import com.attendance.dto.*;
import com.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /** FACULTY: Mark attendance for a student */
    @PostMapping("/mark")
    public ResponseEntity<AttendanceRecordDto> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attendanceService.markAttendance(request, userDetails.getUsername()));
    }

    /** STUDENT: Get full attendance records (all subjects) */
    @GetMapping("/my-report")
    public ResponseEntity<List<AttendanceRecordDto>> getMyReport(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attendanceService.getStudentReport(userDetails.getUsername()));
    }

    /** STUDENT: Get per-subject attendance summary with percentage */
    @GetMapping("/my-summary")
    public ResponseEntity<List<SubjectAttendanceSummary>> getMySummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attendanceService.getStudentSummary(userDetails.getUsername()));
    }

    /** FACULTY: View attendance for a subject on a date */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<AttendanceRecordDto>> getAttendanceBySubject(
            @PathVariable Long subjectId,
            @RequestParam String date) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySubjectAndDate(subjectId, date));
    }
}
