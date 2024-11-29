package com.example.PushOfLife.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinDTO {

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_password1")
    private String userPassword1;

    @JsonProperty("user_password2")
    private String userPassword2;

    @JsonProperty("user_birthday")
    private String userBirthday;

    @JsonProperty("user_gender")
    private String userGender;

    @JsonProperty("user_disease")
    private String userDisease;

    @JsonProperty("user_phone")
    private String userPhone;

    @JsonProperty("user_protector")
    private String userProtector;

}
