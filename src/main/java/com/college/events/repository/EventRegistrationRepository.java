package com.college.events.repository;

import com.college.events.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEventIdOrderByRegisteredAtDesc(Long eventId);
    boolean existsByEventIdAndEmailIgnoreCase(Long eventId, String email);
    long countByEventId(Long eventId);
}
