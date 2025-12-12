package com.example.festival_system.service;

import com.example.festival_system.model.Festival;
import java.util.List;

public interface FestivalService {
    List<Festival> findAll();
    List<Festival> search(String term);
    Festival save(Festival festival);
    Festival findById(Long id);
    void deleteById(Long id);

    List<String> getAllArtists();
    List<String> getAllOrganizers();
    List<String> getAllLocations();

    void delete(Festival existing);
}