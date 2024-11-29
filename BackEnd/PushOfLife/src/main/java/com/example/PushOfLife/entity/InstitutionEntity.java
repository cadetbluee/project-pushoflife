package com.example.PushOfLife.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "institution")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id")
    private Integer id;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "institution_location")
    private String institutionLocation;

    @Column(name = "institution_number")
    private String institutionNumber;

    @Column(name = "institution_latitude")
    private Double institutionLatitude;

    @Column(name = "institution_longitude")
    private Double institutionLongitude;

    @OneToMany(mappedBy = "institution")
    private List<EducationEntity> education;
}
