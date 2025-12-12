package com.example.festival_system.repository;

import com.example.festival_system.model.TicketRegistration;
import com.example.festival_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRegistrationRepository extends JpaRepository<TicketRegistration, Long> {

    long countByFestivalId(Long festivalId);

    long countByFestivalOrganizerId(Long organizerId);
    List<TicketRegistration> findByUser(User user);

    boolean existsByFestivalIdAndUserId(Long festivalId, Long userId);
}
