package com.attendance.controller;

import com.attendance.dto.SubjectDto;
import com.attendance.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/my-subjects")
    public ResponseEntity<List<SubjectDto>> getMySubjects(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subjectService.getSubjectsByFaculty(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<SubjectDto> createSubject(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        String name = (String) body.get("name");
        Long facultyId = Long.valueOf(body.get("facultyId").toString());
        return ResponseEntity.ok(subjectService.createSubject(code, name, facultyId));
    }
}
