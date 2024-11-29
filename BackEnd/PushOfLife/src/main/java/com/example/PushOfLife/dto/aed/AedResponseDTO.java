package com.example.PushOfLife.dto.aed;

import com.example.PushOfLife.entity.AedEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AedResponseDTO {

    @JsonProperty("aed_id")
    private Integer aedId;

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

    public void fromEntity(AedEntity aed) {
        this.aedId = aed.getId();
        this.aedAddress = aed.getAedAddress();
        this.aedPlace = aed.getAedPlace();
        this.aedLocation = aed.getAedLocation();
        this.aedLatitude = aed.getAedLatitude();
        this.aedLongitude = aed.getAedLongitude();
    }


}
