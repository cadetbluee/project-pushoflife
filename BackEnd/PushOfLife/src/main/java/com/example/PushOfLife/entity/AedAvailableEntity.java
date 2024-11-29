package com.example.PushOfLife.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "aed_available")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AedAvailableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_id")
    private Integer id;

    @Column(name = "aed_mon_st_time")
    private LocalTime aedMonStTime;

    @Column(name = "aed_mon_end_time")
    private LocalTime aedMonEndTime;

    @Column(name = "aed_tue_st_time")
    private LocalTime aedTueStTime;

    @Column(name = "aed_tue_end_time")
    private LocalTime aedTueEndTime;

    @Column(name = "aed_wed_st_time")
    private LocalTime aedWedStTime;

    @Column(name = "aed_wed_end_time")
    private LocalTime aedWedEndTime;

    @Column(name = "aed_thu_st_time")
    private LocalTime aedThuStTime;

    @Column(name = "aed_thu_end_time")
    private LocalTime aedThuEndTime;

    @Column(name = "aed_fri_st_time")
    private LocalTime aedFriStTime;

    @Column(name = "aed_fri_end_time")
    private LocalTime aedFriEndTime;

    @Column(name = "aed_sat_st_time")
    private LocalTime aedSatStTime;

    @Column(name = "aed_sat_end_time")
    private LocalTime aedSatEndTime;

    @Column(name = "aed_sun_st_time")
    private LocalTime aedSunStTime;

    @Column(name = "aed_sun_end_time")
    private LocalTime aedSunEndTime;

    @Column(name = "aed_hol_st_time")
    private LocalTime aedHolStTime;

    @Column(name = "aed_hol_end_time")
    private LocalTime aedHolEndTime;

    @Column(name = "aed_fir_sun")
    private Possible aedFirSun;

    @Column(name = "aed_sec_sun")
    private Possible aedSecSun;

    @Column(name = "aed_thi_sun")
    private Possible aedThiSun;

    @Column(name = "aed_fou_sun")
    private Possible aedFouSun;


    public enum Possible{
        Y,
        N,
        NULL
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aed_id")
    private AedEntity aedEntity;

}
