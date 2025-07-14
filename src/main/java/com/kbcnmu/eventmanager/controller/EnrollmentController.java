package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.Enrollment;
import com.kbcnmu.eventmanager.model.Event;
import com.kbcnmu.eventmanager.repository.EventRepository;
import com.kbcnmu.eventmanager.service.EnrollmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/student/{studentId}")
    public List<Enrollment> getEnrollmentsByStudent(@PathVariable Long studentId) {
        return enrollmentService.getEnrollmentsByStudent(studentId);
    }

    @GetMapping("/{eventId}/participants")
    public List<Enrollment> getEnrollmentsByEvent(@PathVariable Long eventId) {
        return enrollmentService.getEnrollmentsByEvent(eventId);
    }

    @DeleteMapping("/{eventId}/student/{studentId}")
    public void removeParticipant(@PathVariable Long eventId, @PathVariable Long studentId) {
        enrollmentService.removeParticipant(eventId, studentId);
    }

    @PostMapping("/{eventId}/student/{studentId}")
    public ResponseEntity<?> enrollStudent(@PathVariable Long eventId, @PathVariable Long studentId) {
        try {
            enrollmentService.enrollStudent(eventId, studentId);

            // ✅ Return updated event with enrollments included
            Event updatedEvent = eventRepository.findByIdWithEnrollments(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred: " + e.getMessage());
        }
    }
}
