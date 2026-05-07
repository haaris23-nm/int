package com.attendance.dto;

import com.attendance.entity.Session;
import com.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MarkAttendanceRequest {
    @NotNull
    private Long studentId;
    @NotNull
    private Long subjectId;
    @NotNull
    private LocalDate date;
    @NotNull
    private Session session;
    @NotNull
    private AttendanceStatus status;
}
