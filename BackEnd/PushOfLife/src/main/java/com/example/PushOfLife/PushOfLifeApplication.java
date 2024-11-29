package com.example.PushOfLife;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class PushOfLifeApplication {
	@PostConstruct
	public void init() {
		// 애플리케이션 전체에 적용될 타임존 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	public static void main(String[] args) {
		SpringApplication.run(PushOfLifeApplication.class, args);
	}

}
