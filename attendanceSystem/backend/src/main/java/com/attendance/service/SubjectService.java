package com.attendance.service;

import com.attendance.dto.SubjectDto;
import com.attendance.entity.Subject;
import com.attendance.entity.User;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.SubjectRepository;
import com.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SubjectDto> getSubjectsByFaculty(String facultyUsername) {
        User faculty = userRepository.findByUsername(facultyUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + facultyUsername));
        return subjectRepository.findByFaculty(faculty).stream().map(this::toDto).collect(Collectors.toList());
    }

    public SubjectDto createSubject(String code, String name, Long facultyId) {
        if (subjectRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Subject code already exists: " + code);
        }
        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + facultyId));
        Subject subject = Subject.builder().code(code).name(name).faculty(faculty).build();
        subjectRepository.save(subject);
        return toDto(subject);
    }

    private SubjectDto toDto(Subject s) {
        return SubjectDto.builder()
                .id(s.getId())
                .code(s.getCode())
                .name(s.getName())
                .facultyName(s.getFaculty() != null ? s.getFaculty().getFullName() : null)
                .facultyId(s.getFaculty() != null ? s.getFaculty().getId() : null)
                .build();
    }
}
