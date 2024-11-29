package com.example.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Service
public class FirebaseInitializeService {

    @Value("${firebase.config.path}")
    String firebasePath;

    @PostConstruct
    public void initialize() {
        try{
            FileInputStream serviceAccount =
                    new FileInputStream(firebasePath);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
