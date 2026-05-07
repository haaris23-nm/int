package com.attendance.repository;

import com.attendance.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /** Prevent duplicate entries for same student/subject/date/session */
    boolean existsByStudentAndSubjectAndDateAndSession(
            User student, Subject subject, LocalDate date, Session session);

    Optional<Attendance> findByStudentAndSubjectAndDateAndSession(
            User student, Subject subject, LocalDate date, Session session);

    /** Student views their own report for a subject */
    List<Attendance> findByStudentAndSubject(User student, Subject subject);

    List<Attendance> findBySubjectAndDate(Subject subject, LocalDate date);

   
    List<Attendance> findByStudent(User student);

    List<Attendance> findBySubjectAndDateAndSession(Subject subject, LocalDate date, Session session);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.subject = :subject AND a.status = 'PRESENT'")
    long countPresentByStudentAndSubject(@Param("student") User student, @Param("subject") Subject subject);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.subject = :subject")
    long countTotalByStudentAndSubject(@Param("student") User student, @Param("subject") Subject subject);
}
