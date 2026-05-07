package com.attendance.repository;

import com.attendance.entity.Subject;
import com.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByFaculty(User faculty);
    boolean existsByCode(String code);
}
