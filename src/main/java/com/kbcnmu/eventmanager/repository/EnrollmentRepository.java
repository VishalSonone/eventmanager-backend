package com.kbcnmu.eventmanager.repository;

import com.kbcnmu.eventmanager.model.Enrollment;
import com.kbcnmu.eventmanager.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventIdAndStudentId(Long eventId, Long studentId);

    void deleteByEventId(Long eventId);

    void deleteByStudentId(Long studentId);

    List<Enrollment> findByEventId(Long eventId);

    List<Enrollment> findByStudentId(Long studentId);

    Optional<Enrollment> findByEventIdAndStudentId(Long eventId, Long studentId);

    void deleteByEventIdAndStudentId(Long eventId, Long studentId);

    // 🔥 Important: Fetch enrollments with event
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.enrollments WHERE e.id = :id")
    Optional<Event> findByIdWithEnrollments(@Param("id") Long id);
}
