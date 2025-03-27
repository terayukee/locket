package com.locket.user.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ClassPathResource("firebase-key.json").getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase 초기화 완료");
            }
        } catch (IOException e) {
            log.error("❌ Firebase 초기화 실패: {}", e.getMessage(), e);
        }
    }
}
