package com.example.dentalscheduler.model;

import com.example.dentalscheduler.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DOCTOR")
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST_NAME")
    @NotNull
    @NotBlank
    private String firstName;

    @Column(name = "LAST_NAME")
    @NotNull
    @NotBlank
    private String lastName;

    @Column(name = "PHONE_NUMBER")
    @NotNull
    @NotBlank
    private String phoneNumber;

    @Column(name = "CNP", nullable = false, unique = true)
    @NotNull
    @NotBlank
    private String CNP;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
