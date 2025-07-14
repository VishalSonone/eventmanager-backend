package com.kbcnmu.eventmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String department;

    @NotBlank
    private String status;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{10,12}$")
    @Column(unique = true)
    private String prn;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$")
    @Column(unique = true)
    private String phone;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String studentClass;


    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("student")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Enrollment> enrollments = new HashSet<>();
}