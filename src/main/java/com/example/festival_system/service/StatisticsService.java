package com.example.festival_system.service;

import com.example.festival_system.model.Festival;
import com.example.festival_system.model.Organizer;
import com.example.festival_system.repository.FestivalRepository;
import com.example.festival_system.repository.TicketRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TicketRegistrationRepository ticketRegistrationRepository;
    private final FestivalRepository festivalRepository;

    public Map<String, Integer> getAllStatistics() {
        Map<String, Integer> map = new LinkedHashMap<>();

        for (Festival f : festivalRepository.findAll()) {
            int count = (int) ticketRegistrationRepository.countByFestivalId(f.getId());
            map.put(f.getName(), count);
        }
        return map;
    }

    public Map<String, Integer> getOrganizerStatistics(Organizer organizer) {
        Map<String, Integer> map = new LinkedHashMap<>();

        for (Festival f : festivalRepository.findByOrganizer(organizer)) {
            int count = (int) ticketRegistrationRepository.countByFestivalId(f.getId());
            map.put(f.getName(), count);
        }
        return map;
    }
}
