package com.example.festival_system.controller;

import com.example.festival_system.model.*;
import com.example.festival_system.repository.OrganizerRepository;
import com.example.festival_system.repository.TicketRepository;
import com.example.festival_system.repository.UserRepository;
import com.example.festival_system.service.FestivalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.festival_system.repository.TicketRegistrationRepository;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/festivals")
public class FestivalWebController {

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private TicketRegistrationRepository ticketRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public String listFestivals(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> artist,
            @RequestParam(required = false) List<String> location,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order,
            Model model,
            Authentication auth
    ) {

        boolean isOrganizer = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        List<Festival> festivals;

        if (isAdmin) {
            festivals = new ArrayList<>(festivalService.findAll());
            model.addAttribute("isOrganizerView", false);
        } else if (isOrganizer) {
            String email = auth.getName();
            Organizer org = organizerRepository.findByEmail(email);
            if (org == null && email.contains("@")) {
                String orgNamePart = email.substring(0, email.indexOf('@')).trim();
                org = organizerRepository.findByNameIgnoreCase(orgNamePart);
            }

            if (org != null) {
                Long orgId = org.getId();
                festivals = festivalService.findAll().stream()
                        .filter(f -> f.getOrganizer() != null && Objects.equals(f.getOrganizer().getId(), orgId))
                        .collect(Collectors.toList());
            } else {
                festivals = Collections.emptyList();
            }
            model.addAttribute("isOrganizerView", true);
        } else {
            festivals = new ArrayList<>(festivalService.findAll());
            model.addAttribute("isOrganizerView", false);
        }

        if (search != null && !search.isBlank()) {
            String term = search.trim().toLowerCase();
            festivals = festivals.stream()
                    .filter(f -> {
                        String orgName = f.getOrganizer() != null && f.getOrganizer().getName() != null
                                ? f.getOrganizer().getName()
                                : f.getOrganizerName();
                        return (f.getName() != null && f.getName().toLowerCase().contains(term))
                                || (f.getLocation() != null && f.getLocation().toLowerCase().contains(term))
                                || (f.getArtists() != null && f.getArtists().toLowerCase().contains(term))
                                || (orgName != null && orgName.toLowerCase().contains(term));
                    })
                    .collect(Collectors.toList());
        }

        if (artist != null && !artist.isEmpty()) {
            Set<String> artistLower = artist.stream()
                    .filter(Objects::nonNull)
                    .map(a -> a.trim().toLowerCase())
                    .collect(Collectors.toSet());
            festivals = festivals.stream()
                    .filter(f -> {
                        if (f.getArtists() == null || f.getArtists().isBlank()) return false;
                        List<String> festArtists = Arrays.stream(f.getArtists().split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(String::toLowerCase)
                                .toList();
                        return festArtists.stream().anyMatch(artistLower::contains);
                    })
                    .collect(Collectors.toList());
        }

        if (location != null && !location.isEmpty()) {
            Set<String> locationsLower = location.stream()
                    .filter(Objects::nonNull)
                    .map(l -> l.trim().toLowerCase())
                    .collect(Collectors.toSet());
            festivals = festivals.stream()
                    .filter(f -> f.getLocation() != null
                            && locationsLower.contains(f.getLocation().trim().toLowerCase()))
                    .collect(Collectors.toList());
        }

        Comparator<Festival> comp = switch (sort) {
            case "startDate" -> Comparator.comparing(Festival::getStartDate, Comparator.nullsFirst(LocalDate::compareTo));
            case "endDate" -> Comparator.comparing(Festival::getEndDate, Comparator.nullsFirst(LocalDate::compareTo));
            case "location" -> Comparator.comparing(Festival::getLocation, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "organizer" -> Comparator.comparing(
                    f -> f.getOrganizer() != null ? f.getOrganizer().getName() : Optional.ofNullable(f.getOrganizerName()).orElse(""),
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            case "artists" -> Comparator.comparing(Festival::getArtists, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            default -> Comparator.comparing(Festival::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        };
        festivals.sort("desc".equalsIgnoreCase(order) ? comp.reversed() : comp);

        model.addAttribute("festivals", festivals);
        model.addAttribute("festival", new Festival());
        model.addAttribute("artists", festivalService.getAllArtists());
        model.addAttribute("locations", festivalService.getAllLocations());
        model.addAttribute("organizers", organizerRepository.findAll());

        model.addAttribute("search", search);
        model.addAttribute("artist", artist);
        model.addAttribute("location", location);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);

        return "festivals";
    }

    @PostMapping("/add")
    public String addFestival(
            @Valid @ModelAttribute("festival") Festival festival,
            BindingResult bindingResult,
            Authentication auth,
            Model model
    ) {

        boolean isOrganizer = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));

        if (isOrganizer) {
            String email = auth.getName();

            Organizer org = organizerRepository.findByEmail(email);

            if (org == null) {
                String orgName = email.substring(0, email.indexOf('@')).trim();
                org = organizerRepository.findByName(orgName);
                if (org == null) {
                    org = new Organizer(orgName);
                    org.setEmail(email);
                    org = organizerRepository.save(org);
                }
            }

            festival.setOrganizer(org);
        }

        else {
            if (festival.getOrganizerName() == null || festival.getOrganizerName().isBlank()) {
                bindingResult.rejectValue("organizerName", "NotBlank", "Организатор обязателен");
            } else {
                Organizer org = organizerRepository.findByName(festival.getOrganizerName().trim());
                if (org == null) {
                    org = new Organizer(festival.getOrganizerName().trim());
                    organizerRepository.save(org);
                }
                festival.setOrganizer(org);
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("festivals", festivalService.findAll());
            model.addAttribute("artists", festivalService.getAllArtists());
            model.addAttribute("locations", festivalService.getAllLocations());
            model.addAttribute("organizers", organizerRepository.findAll());
            return "festivals";
        }

        festivalService.save(festival);
        return "redirect:/festivals";
    }


    @GetMapping("/edit/{id}")
    public String editFestival(
            @PathVariable Long id,
            Model model,
            Authentication auth
    ) {

        Festival f = festivalService.findById(id);
        if (f == null) return "redirect:/festivals";

        boolean isOrganizer = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));

        if (isOrganizer) {
            String email = auth.getName();
            Organizer org = organizerRepository.findByEmail(email);

            if (org == null) {
                String orgName = email.substring(0, email.indexOf('@'));
                org = organizerRepository.findByName(orgName);
            }

            if (org == null || f.getOrganizer() == null ||
                    !f.getOrganizer().getId().equals(org.getId())) {
                return "redirect:/festivals";
            }
        }

        if (f.getOrganizer() != null) {
            f.setOrganizerName(f.getOrganizer().getName());
        }

        model.addAttribute("festival", f);
        model.addAttribute("editingId", id);
        model.addAttribute("festivals", festivalService.findAll());
        model.addAttribute("artists", festivalService.getAllArtists());
        model.addAttribute("locations", festivalService.getAllLocations());
        model.addAttribute("organizers", organizerRepository.findAll());

        model.addAttribute("isOrganizerView", isOrganizer);

        return "festivals";
    }


    @PostMapping("/save")
    public String saveFestival(
            @Valid @ModelAttribute("festival") Festival festival,
            BindingResult bindingResult,
            Model model,
            Authentication auth
    ) {

        boolean isOrganizer = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));

        if (isOrganizer) {
            Festival existing = festivalService.findById(festival.getId());
            if (existing == null) return "redirect:/festivals";

            String email = auth.getName();
            Organizer org = organizerRepository.findByEmail(email);

            if (org == null) {
                String orgName = email.substring(0, email.indexOf('@'));
                org = organizerRepository.findByName(orgName);
            }

            if (org == null || existing.getOrganizer() == null ||
                    !existing.getOrganizer().getId().equals(org.getId())) {
                return "redirect:/festivals";
            }

            festival.setOrganizer(existing.getOrganizer());
        }

        else {
            if (festival.getOrganizerName() != null && !festival.getOrganizerName().isBlank()) {
                Organizer org = organizerRepository.findByName(festival.getOrganizerName().trim());
                if (org == null) {
                    org = new Organizer(festival.getOrganizerName().trim());
                    organizerRepository.save(org);
                }
                festival.setOrganizer(org);
            } else {
                bindingResult.rejectValue("organizerName", "NotBlank", "Организатор обязателен");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("festivals", festivalService.findAll());
            model.addAttribute("artists", festivalService.getAllArtists());
            model.addAttribute("locations", festivalService.getAllLocations());
            model.addAttribute("organizers", organizerRepository.findAll());
            model.addAttribute("editingId", festival.getId());
            return "festivals";
        }

        festivalService.save(festival);
        return "redirect:/festivals";
    }


    @GetMapping("/delete/{id}")
    public String deleteFestival(@PathVariable Long id) {
        Festival existing = festivalService.findById(id);
        if (existing != null) {
            existing.setOrganizer(null);
            try { festivalService.save(existing); } catch (Exception ignored) {}
            try { festivalService.deleteById(id); }
            catch (Exception ex) {
                try { festivalService.delete(existing); } catch (Exception ignored) {}
            }
        }
        return "redirect:/festivals";
    }

    @PostMapping("/register")
    @ResponseBody
    public String registerForFestival(
            @RequestParam Long festivalId,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam int tickets,
            Authentication auth
    ) {
        Festival fest = festivalService.findById(festivalId);
        if (fest == null) return "error";

        String userEmail = (auth != null) ? auth.getName() : email;
        User user = userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(userEmail);
            user.setPassword("{noop}temp");
            user.setRole(Role.USER);
            user = userRepository.save(user);
        }

        TicketRegistration reg = new TicketRegistration();
        reg.setFestival(fest);
        reg.setFullName(fullName);
        reg.setEmail(email);
        reg.setTickets(tickets);
        reg.setUser(user);
        if (ticketRegistrationRepository.existsByFestivalIdAndUserId(festivalId, user.getId())) {
            return "exists";
        }

        ticketRegistrationRepository.save(reg);

        return "success";
    }

    @GetMapping("/mytickets")
    public String myTickets(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        String email = auth.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user = userOpt.orElse(null);

        List<TicketRegistration> registrations = user != null
                ? ticketRegistrationRepository.findByUser(user)
                : Collections.emptyList();
        model.addAttribute("registrations", registrations);
        model.addAttribute("pageTitle", "Мои записи");
        return "mytickets";
    }


}
