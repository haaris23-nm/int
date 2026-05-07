package com.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectAttendanceSummary {
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private long totalClasses;
    private long presentCount;
    private long absentCount;
    private double percentage;
}
