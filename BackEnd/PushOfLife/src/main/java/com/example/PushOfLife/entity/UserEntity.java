package com.example.PushOfLife.entity;

import com.example.PushOfLife.dto.user.UpdateRequestDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Table(name = "user")
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_gender")
    private String userGender;

    @Column(name = "user_disease")
    private String userDisease;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "user_protector")
    private String userProtector;

    @Column(name = "user_fcm")
    private String userFcm;

    @Column(name = "role", nullable = false)
    private String role = "ROLE_USER";

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "profile_url")
    private String profileUrl;

    public void updateInfo(UpdateRequestDTO updateRequest, String profileUrl, String encodedPassword){
        if (encodedPassword != null){
            this.password = encodedPassword;
        }

        if (updateRequest.getUserName()!=null){
            this.userName = updateRequest.getUserName();
        }

        if(updateRequest.getUserGender()!=null){
            this.userGender = updateRequest.getUserGender();
        }

        if(updateRequest.getUserPhone()!=null){
            this.userPhone = updateRequest.getUserPhone();
        }

        if(updateRequest.getUserProtector()!=null){
            this.userProtector = updateRequest.getUserProtector();
        }

        if(updateRequest.getUserFcm()!=null){
            this.userFcm = updateRequest.getUserFcm();
        }

        if(updateRequest.getUserDisease()!=null){
            this.userDisease = updateRequest.getUserDisease();
        }

        if (profileUrl != null){
            this.profileUrl = profileUrl;
        }
    }

}
