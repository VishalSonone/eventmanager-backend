package com.kbcnmu.eventmanager.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties("enrollments")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties("enrollments")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Event event;


    private String status; // optional
    private Integer score; // optional
}
