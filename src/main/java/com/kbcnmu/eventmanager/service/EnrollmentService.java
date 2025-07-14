package com.kbcnmu.eventmanager.service;

import com.kbcnmu.eventmanager.model.Enrollment;
import com.kbcnmu.eventmanager.model.Event;
import com.kbcnmu.eventmanager.model.Student;
import com.kbcnmu.eventmanager.repository.EnrollmentRepository;
import com.kbcnmu.eventmanager.repository.StudentRepository;
import com.kbcnmu.eventmanager.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByEvent(Long eventId) {
        return enrollmentRepository.findByEventId(eventId);
    }

    public void removeParticipant(Long eventId, Long studentId) {
        enrollmentRepository.deleteByEventIdAndStudentId(eventId, studentId);
    }

    public Enrollment enrollStudent(Long eventId, Long studentId) {
        if (enrollmentRepository.existsByEventIdAndStudentId(eventId, studentId)) {
            throw new IllegalStateException("Student already enrolled in this event");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .event(event)
                .build();

        return enrollmentRepository.save(enrollment);
    }
}
