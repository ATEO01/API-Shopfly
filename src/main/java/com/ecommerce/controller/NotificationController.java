package com.ecommerce.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.model.Notification;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.UtilisateurRepository;
import com.ecommerce.service.FirebaseAuthService;
import com.ecommerce.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UtilisateurRepository utilisateurRepository;
    private final FirebaseAuthService firebaseAuthService;
    
    public NotificationController(NotificationService notificationService,
                                  UtilisateurRepository utilisateurRepository,
                                  FirebaseAuthService firebaseAuthService) {
        this.notificationService = notificationService;
        this.utilisateurRepository = utilisateurRepository;
        this.firebaseAuthService = firebaseAuthService;
    }
    
    // Méthode corrigée avec vérification de la longueur du token
    private Utilisateur getUtilisateur(String token) {
        try {
            if (token == null || token.length() < 7) {
                throw new RuntimeException("Token invalide");
            }
            String uid = firebaseAuthService.verifierToken(token.substring(7)).getUid();
            return utilisateurRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        } catch (Exception e) {
            throw new RuntimeException("Erreur d'authentification: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (token == null || token.length() < 7) {
                response.put("notifications", List.of());
                response.put("nonLues", 0);
                return ResponseEntity.ok(response);
            }
            
            Utilisateur utilisateur = getUtilisateur(token);
            List<Notification> notifications = notificationService.getNotifications(utilisateur);
            long nonLues = notificationService.countNonLues(utilisateur);
            
            response.put("notifications", notifications);
            response.put("nonLues", nonLues);
            
        } catch (Exception e) {
            response.put("notifications", List.of());
            response.put("nonLues", 0);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/marquer-tout-lu")
    public ResponseEntity<Map<String, Object>> marquerToutLu(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (token != null && token.length() >= 7) {
                Utilisateur utilisateur = getUtilisateur(token);
                notificationService.marquerToutCommeLu(utilisateur);
            }
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> supprimerNotification(@PathVariable Long id,
                                                                       @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (token != null && token.length() >= 7) {
                Utilisateur utilisateur = getUtilisateur(token);
                notificationService.supprimerNotification(id, utilisateur);
            }
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/tout-supprimer")
    public ResponseEntity<Map<String, Object>> supprimerToutesNotifications(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (token != null && token.length() >= 7) {
                Utilisateur utilisateur = getUtilisateur(token);
                notificationService.supprimerToutesNotifications(utilisateur);
            }
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}