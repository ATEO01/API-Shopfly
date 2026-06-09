//connecte firebase a spring

package com.ecommerce.config;

import java.io.ByteArrayInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    @PostConstruct
    public void initialiserFirebase() {
        try {
            String firebaseconfig = System.getenv("FIREBASE_SERVICE_ACCOUNT");
            InputStream serviceAccount = new ByteArrayInputStream(firebaseconfig.getBytes()); // Pour le déploiement 
            //InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialisé avec succès !");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur Firebase: " + e.getMessage());
        }
    }
}