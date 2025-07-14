package com.kbcnmu.eventmanager.service;

import com.kbcnmu.eventmanager.exception.ResourceNotFoundException;
import com.kbcnmu.eventmanager.model.Enrollment;
import com.kbcnmu.eventmanager.model.Event;
import com.kbcnmu.eventmanager.model.Student;
import com.kbcnmu.eventmanager.repository.EnrollmentRepository;
import com.kbcnmu.eventmanager.repository.EventRepository;
import com.kbcnmu.eventmanager.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAllWithEnrollments(); // ✅ use this instead of findAll()
    }
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateAfterWithEnrollments(LocalDate.now());
    }

    public List<Event> getCompletedEvents() {
        return eventRepository.findByDateBeforeWithEnrollments(LocalDate.now());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            event.setName(updatedEvent.getName());
            event.setDescription(updatedEvent.getDescription());
            event.setType(updatedEvent.getType());
            event.setDate(updatedEvent.getDate());
            event.setVenue(updatedEvent.getVenue());
            event.setOrganizer(updatedEvent.getOrganizer());
            return eventRepository.save(event);
        });
    }

    @Transactional
    public void enrollStudent(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (enrollmentRepository.existsByEventIdAndStudentId(eventId, studentId)) {
            throw new RuntimeException("Student already enrolled in event");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setEvent(event);
        enrollment.setStudent(student);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void unenrollStudent(Long eventId, Long studentId) {
        enrollmentRepository.deleteByEventIdAndStudentId(eventId, studentId);
    }

    public void deleteEvent(Long id) {
        enrollmentRepository.deleteByEventId(id);
        eventRepository.deleteById(id);
    }
    public Optional<Event> getEventWithParticipants(Long id) {
        return eventRepository.findByIdWithEnrollments(id);
    }

}
