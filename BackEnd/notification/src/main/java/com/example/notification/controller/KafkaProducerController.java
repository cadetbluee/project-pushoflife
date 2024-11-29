package com.example.notification.controller;

import com.example.notification.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/kafka")
@RestController
@RequiredArgsConstructor
public class KafkaProducerController {

    private final KafkaProducerService kafkaProducerService;
    @Value("${test.fcmKey}")
    private String testFcmKey;

    @PostMapping
    public ResponseEntity<?> sendMessage() {

        double longitude = 123.455
                , latitude = 456.678;

        try{
            kafkaProducerService.sendTopic(testFcmKey,longitude,latitude);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
}
