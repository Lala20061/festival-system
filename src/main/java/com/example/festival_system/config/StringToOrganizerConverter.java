package com.example.festival_system.config;


import com.example.festival_system.model.Organizer;
import com.example.festival_system.repository.OrganizerRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class StringToOrganizerConverter implements Converter<String, Organizer> {

    @Autowired
    private OrganizerRepository organizerRepository;

    @Override
    public Organizer convert(String source) {
        if (source == null) return null;
        String trimmed = source.trim();
        if (trimmed.isEmpty()) return null;

        try {
            Long id = Long.parseLong(trimmed);
            return organizerRepository.findById(id).orElse(null);
        } catch (NumberFormatException ignored) {
        }

        Organizer byName = organizerRepository.findByName(trimmed);
        return byName;
    }
}

