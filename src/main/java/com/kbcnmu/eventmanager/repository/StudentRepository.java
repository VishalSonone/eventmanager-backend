package com.kbcnmu.eventmanager.repository;

import com.kbcnmu.eventmanager.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Student entities.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find a student by email.
     */
    Optional<Student> findByEmail(String email);

    /**
     * Find a student by PRN.
     */
    Optional<Student> findByPrn(String prn);

    /**
     * Find a student by phone number.
     */
    Optional<Student> findByPhone(String phone);

    /**
     * Check if a student with the given email already exists.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a student with the given PRN already exists.
     */
    boolean existsByPrn(String prn);

    /**
     * Check if a student with the given phone number already exists.
     */
    boolean existsByPhone(String phone);

    /**
     * Get students by their status (e.g., approved, pending, rejected).
     */
    List<Student> findByStatus(String status);

    /**
     * Fetch a student along with their registered events.
     */
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.enrollments e LEFT JOIN FETCH e.event WHERE s.id = :studentId")
    Optional<Student> findByIdWithEnrollments(@Param("studentId") Long studentId);
}
