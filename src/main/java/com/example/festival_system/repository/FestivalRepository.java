package com.example.festival_system.repository;

import com.example.festival_system.model.Festival;
import com.example.festival_system.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    @Query("SELECT f FROM Festival f WHERE " +
            "LOWER(f.name) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(f.location) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(f.organizer.name) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(f.artists) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Festival> search(@Param("term") String term);

    List<Festival> findByOrganizerId(Long id);

    List<Festival> findByOrganizer(Organizer organizer);
}
