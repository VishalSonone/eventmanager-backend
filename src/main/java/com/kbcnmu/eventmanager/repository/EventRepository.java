package com.kbcnmu.eventmanager.repository;

import com.kbcnmu.eventmanager.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.enrollments")
    List<Event> findAllWithEnrollments();

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.enrollments WHERE e.date > :date")
    List<Event> findByDateAfterWithEnrollments(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.enrollments WHERE e.date < :date")
    List<Event> findByDateBeforeWithEnrollments(@Param("date") LocalDate date);
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.enrollments WHERE e.id = :eventId")
    Optional<Event> findByIdWithEnrollments(@Param("eventId") Long eventId);

}
