package com.example.PushOfLife.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "education")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Integer id;

    @Column(name = "education_name")
    private String educationName;

    @Column(name = "education_date")
    private LocalDate educationDate;

    @Column(name = "education_time")
    private LocalTime educationTime;

    @Column(name = "education_person")
    private Integer educationPerson;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private InstitutionEntity institution;

    @OneToMany(mappedBy = "education")
    private List<ReservationEntity> reservation;
}
