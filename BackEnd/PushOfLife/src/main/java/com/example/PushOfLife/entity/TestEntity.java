package com.example.PushOfLife.entity;

import com.example.PushOfLife.dto.test.TestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Getter
@Builder
@Table(name = "Test")
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="test")
    private String test;

    public void updateTest(TestDTO test){
        this.test = test.getTest();
    }

}
