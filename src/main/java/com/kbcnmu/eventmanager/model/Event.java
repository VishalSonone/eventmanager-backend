package com.kbcnmu.eventmanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String type;
    private LocalDate date;
    private String venue;
    private String organizer;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("event")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Enrollment> enrollments = new HashSet<>();

}
