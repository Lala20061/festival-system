package com.example.festival_system.repository;


import com.example.festival_system.model.Ticket;
import com.example.festival_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);
}