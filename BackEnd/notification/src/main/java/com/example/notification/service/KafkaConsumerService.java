package com.example.notification.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;
    @Value("${test.fcmKey}")
    private String testFcmKey;

    @KafkaListener(topics="${topic.name}",groupId = "${spring.kafka.consumer.group-id}")
    public Mono<Void> consumeNotify(String message) throws IOException{
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> messageMap = objectMapper.readValue(message, Map.class);

            String fcmToken = messageMap.get("fcmToken");
            String longitude = messageMap.get("longitude");
            String latitude = messageMap.get("latitude");

            System.out.println("Received message: " + message);
            return this.notificationService.sendNotification(fcmToken, longitude, latitude, false);
//            return this.notificationService.sendNotification(testFcmKey, longitude, latitude, false);

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(e);
        }

    }

}
