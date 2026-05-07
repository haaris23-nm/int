package com.attendance;

import com.attendance.entity.*;
import com.attendance.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        log.info("Seeding database with demo data...");

        // ── Users ────────────────────────────────────────────────────────
        User admin = User.builder().username("admin").passwordHash(passwordEncoder.encode("admin123"))
                .email("admin@university.edu").fullName("System Admin").role(Role.ADMIN).build();

        User faculty1 = User.builder().username("prof.smith").passwordHash(passwordEncoder.encode("faculty123"))
                .email("smith@university.edu").fullName("Dr. Alice Smith").role(Role.FACULTY).build();

        User faculty2 = User.builder().username("prof.jones").passwordHash(passwordEncoder.encode("faculty123"))
                .email("jones@university.edu").fullName("Prof. Bob Jones").role(Role.FACULTY).build();

        User student1 = User.builder().username("john.doe").passwordHash(passwordEncoder.encode("student123"))
                .email("john@university.edu").fullName("John Doe").role(Role.STUDENT).build();

        User student2 = User.builder().username("jane.smith").passwordHash(passwordEncoder.encode("student123"))
                .email("jane@university.edu").fullName("Jane Smith").role(Role.STUDENT).build();

        User student3 = User.builder().username("alex.kumar").passwordHash(passwordEncoder.encode("student123"))
                .email("alex@university.edu").fullName("Alex Kumar").role(Role.STUDENT).build();

        userRepository.saveAll(List.of(admin, faculty1, faculty2, student1, student2, student3));

        // ── Subjects ─────────────────────────────────────────────────────
        Subject cs101 = Subject.builder().code("CS101").name("Data Structures").faculty(faculty1).build();
        Subject cs102 = Subject.builder().code("CS102").name("Algorithms").faculty(faculty1).build();
        Subject cs201 = Subject.builder().code("CS201").name("Database Systems").faculty(faculty2).build();
        Subject cs202 = Subject.builder().code("CS202").name("Operating Systems").faculty(faculty2).build();
        subjectRepository.saveAll(List.of(cs101, cs102, cs201, cs202));

        // ── Seed Attendance (last 10 days) ───────────────────────────────
        List<User> students = List.of(student1, student2, student3);
        List<Subject> subjects = List.of(cs101, cs102, cs201, cs202);
        LocalDate today = LocalDate.now();

        for (User student : students) {
            for (Subject subject : subjects) {
                for (int i = 9; i >= 0; i--) {
                    LocalDate date = today.minusDays(i);
                    // Skip weekends
                    if (date.getDayOfWeek().getValue() >= 6) continue;
                    AttendanceStatus status = (Math.random() > 0.25)
                            ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
                    Attendance att = Attendance.builder()
                            .student(student).subject(subject).date(date)
                            .session(Session.MORNING).status(status).markedBy(subject.getFaculty())
                            .build();
                    attendanceRepository.save(att);
                }
            }
        }

        log.info("✅ Seeding complete!");
        log.info("─────────────────────────────────────────────────────────");
        log.info("  ADMIN:   admin / admin123");
        log.info("  FACULTY: prof.smith / faculty123");
        log.info("  FACULTY: prof.jones / faculty123");
        log.info("  STUDENT: john.doe / student123");
        log.info("  STUDENT: jane.smith / student123");
        log.info("  STUDENT: alex.kumar / student123");
        log.info("─────────────────────────────────────────────────────────");
    }
}
