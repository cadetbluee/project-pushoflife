package com.example.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    @Value("${topic.name}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendTopic(String fcmToken, double longitude, double latitude){

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("fcmToken", fcmToken);
        messageMap.put("longitude", String.valueOf(longitude));
        messageMap.put("latitude", String.valueOf(latitude));
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String messageJson = objectMapper.writeValueAsString(messageMap);
            System.out.printf("Error message sent : %s%n",messageJson);
            this.kafkaTemplate.send(topic, messageJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
