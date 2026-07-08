//connecte firebase a spring

package com.ecommerce.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    @PostConstruct
    public void initialiserFirebase() {
        try {
            InputStream serviceAccount;
            
            // 1. Essayer de charger depuis les variables d'environnement (Render)
            String firebaseConfigEnv = System.getenv("FIREBASE_SERVICE_ACCOUNT");
            
            if (firebaseConfigEnv != null && !firebaseConfigEnv.isEmpty()) {
                // Convertir la chaîne JSON en InputStream
                serviceAccount = new java.io.ByteArrayInputStream(firebaseConfigEnv.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                System.out.println("📁 Firebase: chargé depuis variable d'environnement");
            } else {
                // 2. Essayer de charger depuis le classpath (local)
                try {
                    serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
                    System.out.println("📁 Firebase: chargé depuis classpath");
                } catch (Exception e) {
                    // 3. Essayer depuis le système de fichiers
                    try {
                        serviceAccount = new FileSystemResource("firebase-service-account.json").getInputStream();
                        System.out.println("📁 Firebase: chargé depuis système de fichiers");
                    } catch (Exception e2) {
                        throw new RuntimeException("❌ Aucun fichier de service account trouvé");
                    }
                }
            }
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialisé avec succès !");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}