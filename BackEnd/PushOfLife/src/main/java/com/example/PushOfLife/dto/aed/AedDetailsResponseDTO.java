package com.example.PushOfLife.dto.aed;

import com.example.PushOfLife.entity.AedAvailableEntity;
import com.example.PushOfLife.entity.AedEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AedDetailsResponseDTO {

    @JsonProperty(value = "aed_address")
    private String aedAddress;

    @JsonProperty(value = "aed_place")
    private String aedPlace;

    @JsonProperty(value = "aed_location")
    private String aedLocation;

    @JsonProperty(value = "aed_latitude")
    private Double aedLatitude;

    @JsonProperty(value = "aed_longitude")
    private Double aedLongitude;

    @JsonProperty(value = "aed_number")
    private String aedNumber;

    @JsonProperty(value = "aed_mon_st_time")
    private LocalTime aedMonStTime;

    @JsonProperty(value = "aed_mon_end_time")
    private LocalTime aedMonEndTime;

    @JsonProperty(value = "aed_tue_st_time")
    private LocalTime aedTueStTime;

    @JsonProperty(value = "aed_tue_end_time")
    private LocalTime aedTueEndTime;

    @JsonProperty(value = "aed_wed_st_time")
    private LocalTime aedWedStTime;

    @JsonProperty(value = "aed_wed_end_time")
    private LocalTime aedWedEndTime;

    @JsonProperty(value = "aed_thu_st_time")
    private LocalTime aedThuStTime;

    @JsonProperty(value = "aed_thu_end_time")
    private LocalTime aedThuEndTime;

    @JsonProperty(value = "aed_fri_st_time")
    private LocalTime aedFriStTime;

    @JsonProperty(value = "aed_fri_end_time")
    private LocalTime aedFriEndTime;

    @JsonProperty(value = "aed_sat_st_time")
    private LocalTime aedSatStTime;

    @JsonProperty(value = "aed_sat_end_time")
    private LocalTime aedSatEndTime;

    @JsonProperty(value = "aed_sun_st_time")
    private LocalTime aedSunStTime;

    @JsonProperty(value = "aed_sun_end_time")
    private LocalTime aedSunEndTime;

    @JsonProperty(value = "aed_hol_st_time")
    private LocalTime aedHolStTime;

    @JsonProperty(value = "aed_hol_end_time")
    private LocalTime aedHolEndTime;

    @JsonProperty(value = "aed_fir_sun")
    private AedAvailableEntity.Possible aedFirSun;

    @JsonProperty(value = "aed_sec_sun")
    private AedAvailableEntity.Possible aedSecSun;

    @JsonProperty(value = "aed_thi_sun")
    private AedAvailableEntity.Possible aedThiSun;

    @JsonProperty(value = "aed_fou_sun")
    private AedAvailableEntity.Possible aedFouSun;

    public void fromEntity(AedEntity aed, AedAvailableEntity available){
        this.aedAddress = aed.getAedAddress();
        this.aedPlace = aed.getAedPlace();
        this.aedLocation = aed.getAedLocation();
        this.aedLatitude = aed.getAedLatitude();
        this.aedLongitude = aed.getAedLongitude();
        this.aedNumber = aed.getAedNumber();
        this.aedMonStTime = available.getAedMonStTime();
        this.aedMonEndTime = available.getAedMonEndTime();
        this.aedTueStTime = available.getAedTueStTime();
        this.aedTueEndTime = available.getAedTueEndTime();
        this.aedWedStTime = available.getAedWedStTime();
        this.aedWedEndTime = available.getAedWedEndTime();
        this.aedThuStTime = available.getAedThuStTime();
        this.aedThuEndTime = available.getAedThuEndTime();
        this.aedFriStTime = available.getAedFriStTime();
        this.aedFriEndTime = available.getAedFriEndTime();
        this.aedSatStTime = available.getAedSatStTime();
        this.aedSatEndTime = available.getAedSatEndTime();
        this.aedSunStTime = available.getAedSunStTime();
        this.aedSunEndTime = available.getAedSunEndTime();
        this.aedHolStTime = available.getAedHolStTime();
        this.aedHolEndTime = available.getAedHolEndTime();
        this.aedFirSun = available.getAedFirSun();
        this.aedSecSun = available.getAedSecSun();
        this.aedThiSun = available.getAedThiSun();
        this.aedFouSun= available.getAedFouSun();
    }
}
