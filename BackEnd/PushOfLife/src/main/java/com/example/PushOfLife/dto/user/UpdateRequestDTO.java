package com.example.PushOfLife.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRequestDTO {

    @JsonProperty(value = "user_password1", required = false)
    private String userPassword1;

    @JsonProperty(value = "user_password2", required = false)
    private String userPassword2;

    @JsonProperty(value = "user_name", required = false)
    private String userName;

    @JsonProperty(value = "user_birthday", required = false)
    private String userBirthday;

    @JsonProperty(value = "user_gender", required = false)
    private String userGender;

    @JsonProperty(value = "user_disease", required = false)
    private String userDisease;

    @JsonProperty(value = "user_protector", required = false)
    private String userProtector;

}
