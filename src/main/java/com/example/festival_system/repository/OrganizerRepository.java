package com.example.festival_system.repository;

import com.example.festival_system.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
    Organizer findByName(String name);
    Organizer findByEmail(String email);
    Organizer findByNameIgnoreCase(String name);
}