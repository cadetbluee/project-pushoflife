package com.example.PushOfLife.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "aed")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aed_id")
    private Integer id;

    @Column(name = "aed_address")
    private String aedAddress;

    @Column(name = "aed_place")
    private String aedPlace;

    @Column(name = "aed_location")
    private String aedLocation;

    @Column(name = "aed_latitude")
    private Double aedLatitude;

    @Column(name = "aed_longitude")
    private Double aedLongitude;

    @Column(name = "aed_number")
    private String aedNumber;

    @OneToOne(mappedBy = "aedEntity")
    private AedAvailableEntity aedAvailableEntity;
}
