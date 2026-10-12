package com.college.events.controller;

import com.college.events.model.CollegeEvent;
import com.college.events.model.EventRegistration;
import com.college.events.repository.CollegeEventRepository;
import com.college.events.repository.EventRegistrationRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final CollegeEventRepository events;
    private final EventRegistrationRepository registrations;

    public EventController(CollegeEventRepository events, EventRegistrationRepository registrations) {
        this.events = events;
        this.registrations = registrations;
    }

    @GetMapping
    public List<CollegeEvent> listEvents() {
        return events.findAllByOrderByEventDateAscEventTimeAsc();
    }

    @GetMapping("/{id}")
    public CollegeEvent getEvent(@PathVariable Long id) {
        return events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CollegeEvent createEvent(@Valid @RequestBody CollegeEvent event) {
        event.setId(null);
        return events.save(event);
    }

    @PutMapping("/{id}")
    public CollegeEvent updateEvent(@PathVariable Long id, @Valid @RequestBody CollegeEvent incoming) {
        CollegeEvent event = events.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        event.setTitle(incoming.getTitle());
        event.setDescription(incoming.getDescription());
        event.setVenue(incoming.getVenue());
        event.setEventDate(incoming.getEventDate());
        event.setEventTime(incoming.getEventTime());
        event.setOrganizer(incoming.getOrganizer());
        event.setCapacity(incoming.getCapacity());
        return events.save(event);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteEvent(@PathVariable Long id) {
        CollegeEvent event = events.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        if (registrations.countByEventId(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete an event with registrations.");
        }
        events.delete(event);
        return Map.of("message", "Event deleted");
    }

    @GetMapping("/{id}/registrations")
    public List<EventRegistration> listRegistrations(@PathVariable Long id) {
        if (!events.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        return registrations.findByEventIdOrderByRegisteredAtDesc(id);
    }

    @PostMapping("/{id}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public EventRegistration register(@PathVariable Long id, @Valid @RequestBody EventRegistration request) {
        CollegeEvent event = events.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        if (registrations.existsByEventIdAndEmailIgnoreCase(id, request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered for this event.");
        }
        if (registrations.countByEventId(id) >= event.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This event has reached its capacity.");
        }
        request.setId(null);
        request.setEvent(event);
        request.setRegisteredAt(java.time.LocalDateTime.now());
        return registrations.save(request);
    }
}
