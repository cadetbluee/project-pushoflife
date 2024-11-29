package com.example.notification.controller;

import com.example.notification.service.NotificationService;
import com.google.api.HttpBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify-test")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @Value("${test.fcmKey}")
    private String testFcmKey;

    @PostMapping()
    public ResponseEntity<Void> sendMessage() {
        System.out.println(testFcmKey);
        for (int i=0; i<250; i++){
            System.out.println(i);
            try{
                notificationService.sendNotNormal(testFcmKey, "123","123");

            } catch (Exception e){
                e.printStackTrace();
            }

        }
        return ResponseEntity.ok().build();

    }
}
