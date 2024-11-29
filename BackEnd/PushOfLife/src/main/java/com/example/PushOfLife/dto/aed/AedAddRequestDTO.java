package com.example.PushOfLife.dto.aed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AedAddRequestDTO {

    @JsonProperty(value = "aed_latitude")
    private double aedLatitude;

    @JsonProperty(value ="aed_longitude")
    private double aedLongitude;

    @JsonProperty(value ="aed_place")
    private String aedPlace;

    @JsonProperty(value ="aed_location")
    private String aedLocation;

    @JsonProperty(value ="aed_address")
    private String aedAddress;

    @JsonProperty(value ="aed_number")
    private String aedNumber;
}
