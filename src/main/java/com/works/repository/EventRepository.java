package com.works.repository;

import com.works.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatus(com.works.entity.EventStatus status, org.springframework.data.domain.Pageable pageable);
    @org.springframework.data.jpa.repository.Query("SELECT e FROM Event e WHERE e.status = :status AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    org.springframework.data.domain.Page<Event> searchActiveEvents(
            @org.springframework.data.repository.query.Param("status") com.works.entity.EventStatus status,
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable
    );
}