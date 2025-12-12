package com.example.festival_system.service;

import com.example.festival_system.model.Festival;
import com.example.festival_system.repository.FestivalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FestivalServiceImpl implements FestivalService {

    private final FestivalRepository festivalRepository;

    public FestivalServiceImpl(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    @Override
    public List<Festival> findAll() {
        return festivalRepository.findAll();
    }

    @Override
    public List<Festival> search(String term) {
        if (term == null || term.isBlank()) return findAll();
        return festivalRepository.search(term.trim());
    }

    @Override
    public Festival save(Festival festival) {
        return festivalRepository.save(festival);
    }

    @Override
    public Festival findById(Long id) {
        return festivalRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        festivalRepository.deleteById(id);
    }

    @Override
    public List<String> getAllArtists() {
        return festivalRepository.findAll().stream()
                .map(Festival::getArtists)
                .filter(Objects::nonNull)
                .flatMap(artists -> java.util.Arrays.stream(artists.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Override
    public List<String> getAllOrganizers() {
        return festivalRepository.findAll().stream()
                .map(Festival::getOrganizer)
                .filter(Objects::nonNull)
                .map(org -> org.getName())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Override
    public List<String> getAllLocations() {
        return festivalRepository.findAll().stream()
                .map(Festival::getLocation)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Override
    public void delete(Festival existing) {

    }
}