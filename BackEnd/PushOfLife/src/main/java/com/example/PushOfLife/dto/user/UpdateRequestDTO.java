package com.example.PushOfLife.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDTO {

    @JsonProperty(value = "password1", required = false)
    private String password1;

    @JsonProperty(value = "password2", required = false)
    private String password2;

    @JsonProperty(value = "user_name", required = false)
    private String userName;

    @JsonProperty(value = "user_gender", required = false)
    private String userGender;

    @JsonProperty(value = "user_disease", required = false)
    private String userDisease;

    @JsonProperty(value = "user_phone", required = false)
    private String userPhone;

    @JsonProperty(value = "user_protector", required = false)
    private String userProtector;

    @JsonProperty(value = "user_fcm", required = false)
    private String userFcm;

}
