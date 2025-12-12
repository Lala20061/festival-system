package com.example.festival_system.controller;

import com.example.festival_system.model.Festival;
import com.example.festival_system.model.Organizer;
import com.example.festival_system.repository.FestivalRepository;
import com.example.festival_system.repository.TicketRegistrationRepository;
import com.example.festival_system.repository.UserRepository;
import com.example.festival_system.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StatsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private TicketRegistrationRepository ticketRegistrationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @GetMapping("/stats")
    public String stats(Model model, Authentication auth) {

        boolean isOrganizer = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));
        model.addAttribute("isOrganizer", isOrganizer);

        if (isOrganizer) {
            String email = auth.getName();
            String orgName = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            Organizer org = organizerRepository.findByName(orgName);

            if (org == null) {
                model.addAttribute("error", "Организатор не найден.");
                return "stats";
            }

            List<Festival> myFests = festivalRepository.findByOrganizerId(org.getId());

            long myFestCount = myFests.size();

            long myRegistrations = ticketRegistrationRepository
                    .countByFestivalOrganizerId(org.getId());

            double avgArtists = myFests.stream()
                    .filter(f -> f.getArtists() != null && !f.getArtists().trim().isEmpty())
                    .mapToLong(f -> f.getArtists().split(",").length)
                    .average()
                    .orElse(0.0);

            model.addAttribute("isOrganizer", true);
            model.addAttribute("myFestCount", myFestCount);
            model.addAttribute("myRegistrations", myRegistrations);
            model.addAttribute("myAverageArtists", String.format("%.1f", avgArtists));

            return "stats";
        }

        long totalUsers = userRepository.count();
        long totalFestivals = festivalRepository.count();
        long totalRegistrations = ticketRegistrationRepository.count();

        double avgArtistsPerFest = festivalRepository.findAll().stream()
                .filter(f -> f.getArtists() != null && !f.getArtists().trim().isEmpty())
                .mapToLong(f -> f.getArtists().split(",").length)
                .average()
                .orElse(0.0);

        model.addAttribute("isOrganizer", false);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalFestivals", totalFestivals);
        model.addAttribute("totalRegistrations", totalRegistrations);
        model.addAttribute("avgArtistsPerFest", String.format("%.1f", avgArtistsPerFest));

        return "stats";
    }
}
