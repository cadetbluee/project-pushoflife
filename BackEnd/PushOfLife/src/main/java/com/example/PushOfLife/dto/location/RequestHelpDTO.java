package com.example.PushOfLife.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestHelpDTO {
    @JsonProperty("fcm_token")
    private String fcmToken;
}
