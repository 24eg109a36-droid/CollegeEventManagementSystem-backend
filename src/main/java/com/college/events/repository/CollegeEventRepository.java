package com.college.events.repository;

import com.college.events.model.CollegeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollegeEventRepository extends JpaRepository<CollegeEvent, Long> {
    List<CollegeEvent> findAllByOrderByEventDateAscEventTimeAsc();
}
