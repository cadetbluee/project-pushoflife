package com.example.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaProducerService kafkaProducerService;

    public Mono<Void> sendNotification(String fcmToken, String longitude, String latitude, boolean isError){
        String title = "구조 요청 알림";
        String body = "근처에 도움이 필요한 환자가 발생했습니다.";

        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .putData("latitude",latitude)
                .putData("longitude",longitude)
                .setToken(fcmToken)
                .build();

        try{
            System.out.println(message.toString());

            String response = FirebaseMessaging.getInstance().send(message);

            System.out.println("Successfully sent Message : "+response);

        } catch (Exception e){
            if (!isError){
                System.out.println("failed"+e);
//                return this.sendNotification(fcmToken, longitude,latitude,true);
            }
        }

        return Mono.empty();
    }

    public void sendNotNormal(String fcmToken, String longitude, String latitude){
        String title = "테스트용 알림";
        String body = "속도 비교용 함수입니다.";

        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .putData("latitude",latitude)
                .putData("longitude",longitude)
                .setToken(fcmToken)
                .build();

        try{
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent Message : "+response);

        } catch (Exception e){

        }
    }

}
