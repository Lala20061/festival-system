package com.example.festival_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "festival")
public class Festival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Это поле обязательно")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Дата начала обязательна")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Дата окончания обязательна")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotBlank(message = "Место обязательно")
    @Column
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String website;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @NotBlank(message = "Артисты обязательны")
    @Column(columnDefinition = "TEXT")
    private String artists;

    @Transient
    private String organizerName;

    public Festival() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Organizer getOrganizer() { return organizer; }
    public void setOrganizer(Organizer organizer) { this.organizer = organizer; }

    public String getArtists() { return artists; }
    public void setArtists(String artists) { this.artists = artists; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getOrganizerDisplayName() {
        if (organizer != null && organizer.getName() != null && !organizer.getName().isBlank()) {
            return organizer.getName();
        }
        return (organizerName != null && !organizerName.isBlank()) ? organizerName : "—";
    }
}
