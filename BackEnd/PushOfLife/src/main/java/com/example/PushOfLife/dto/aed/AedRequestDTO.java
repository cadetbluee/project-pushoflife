package com.example.PushOfLife.dto.aed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AedRequestDTO {

    @JsonProperty(value = "nor_latitude", required = true)
    private double norLatitude;

    @JsonProperty(value = "nor_longitude", required = true)
    private double norLongitude;

    @JsonProperty(value = "sou_latitude", required = true)
    private double souLatitude;

    @JsonProperty(value = "sou_longitude", required = true)
    private double souLongitude;

}
