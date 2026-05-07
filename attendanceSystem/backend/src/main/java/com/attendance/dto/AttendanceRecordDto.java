package com.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecordDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentUsername;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private String date;
    private String session;
    private String status;
    private String markedByName;
}
