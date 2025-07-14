package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.Event;
import com.kbcnmu.eventmanager.service.EventService;
import com.kbcnmu.eventmanager.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    // 🔁 Fetch all events (with enrollments if needed inside service)
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // 🔍 Get event by ID (basic info)
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id).orElse(null);
    }

    // 📦 Fetch event by ID with enrollments (used after enroll)
    @GetMapping("/{id}/with-participants")
    public Event getEventWithParticipants(@PathVariable Long id) {
        return eventRepository.findByIdWithEnrollments(id).orElse(null);
    }

    // ⏳ Upcoming events
    @GetMapping("/upcoming")
    public List<Event> getUpcomingEvents() {
        return eventService.getUpcomingEvents();
    }

    // ✅ Completed events
    @GetMapping("/completed")
    public List<Event> getCompletedEvents() {
        return eventService.getCompletedEvents();
    }

    // ➕ Add new event
    @PostMapping
    public Event addEvent(@RequestBody Event event) {
        return eventService.addEvent(event);
    }

    // ✏️ Update existing event
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        return eventService.updateEvent(id, updatedEvent).orElse(null);
    }

    // ❌ Delete event
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
    

}
