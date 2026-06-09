//reçoit les commandes d'angular 

package com.ecommerce.controller;

import com.ecommerce.dto.InscriptionRequest;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.UtilisateurRepository;
import com.ecommerce.service.FirebaseAuthService;
import com.ecommerce.service.UtilisateurService;
import com.ecommerce.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UtilisateurService utilisateurService;
    private final FirebaseAuthService firebaseAuthService;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    
    public AuthController(UtilisateurService utilisateurService, 
                          FirebaseAuthService firebaseAuthService,
                          UtilisateurRepository utilisateurRepository,
                          NotificationService notificationService) {
        this.utilisateurService = utilisateurService;
        this.firebaseAuthService = firebaseAuthService;
        this.utilisateurRepository = utilisateurRepository;
        this.notificationService = notificationService;
    }
    
    @PostMapping("/inscription")
    public ResponseEntity<Map<String, Object>> inscription(@RequestBody InscriptionRequest request) {
        return ResponseEntity.ok(utilisateurService.inscription(request));
    }
    
    @PostMapping("/connexion")
    public ResponseEntity<Map<String, Object>> connexion(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(utilisateurService.connexion(request));
    }
    
@GetMapping("/role")
public ResponseEntity<Map<String, Object>> getUserRole(@RequestHeader("Authorization") String token) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        String firebaseToken = token.substring(7);
        var decodedToken = firebaseAuthService.verifierToken(firebaseToken);
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        
        System.out.println("🔍 UID du token: " + uid);
        System.out.println("📧 Email du token: " + email);
        
        Optional<Utilisateur> userOpt = utilisateurRepository.findByFirebaseUid(uid);
        
        if (userOpt.isPresent()) {
            System.out.println("✅ Utilisateur trouvé par UID: " + userOpt.get().getEmail());
            response.put("role", userOpt.get().getRole());
        } else {
            // Essayer par email
            userOpt = utilisateurRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                System.out.println("⚠️ Utilisateur trouvé par EMAIL mais pas par UID!");
                System.out.println("   → Mise à jour de l'UID: " + uid);
                Utilisateur user = userOpt.get();
                user.setFirebaseUid(uid);
                utilisateurRepository.save(user);
                response.put("role", user.getRole());
            } else {
                System.out.println("❌ Utilisateur non trouvé");
                response.put("role", "VISITEUR");
            }
        }
        
        response.put("success", true);
        
    } catch (Exception e) {
        e.printStackTrace();
        response.put("success", false);
        response.put("role", "VISITEUR");
    }
    
    return ResponseEntity.ok(response);
}
    
    // Récupérer tous les utilisateurs (admin seulement)
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Map<String, Object>>> getAllUtilisateurs(@RequestHeader("Authorization") String token) {
        List<Map<String, Object>> resultats = new ArrayList<>();
        
        try {
            String firebaseToken = token.substring(7);
            var decodedToken = firebaseAuthService.verifierToken(firebaseToken);
            String uid = decodedToken.getUid();
            
            Utilisateur admin = utilisateurRepository.findByFirebaseUid(uid).orElse(null);
            // Vérifier que l'utilisateur est admin (ADMIN ou ADMIN_PRINCIPAL)
            if (admin == null || (!"ADMIN".equals(admin.getRole()) && !"ADMIN_PRINCIPAL".equals(admin.getRole()))) {
                return ResponseEntity.status(403).body(resultats);
            }
            
            // Récupérer tous les utilisateurs
            List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
            
            for (Utilisateur u : utilisateurs) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", u.getId());
                user.put("email", u.getEmail());
                user.put("prenom", u.getPrenom());
                user.put("nom", u.getNom());
                user.put("role", u.getRole());
                user.put("dateInscription", u.getDateInscription() != null ? u.getDateInscription().toString() : "");
                resultats.add(user);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(resultats);
    }
    
    // Changer le rôle d'un utilisateur (admin principal)
    @PutMapping("/utilisateurs/{id}/role")
    public ResponseEntity<Map<String, Object>> changerRoleUtilisateur(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String firebaseToken = token.substring(7);
            var decodedToken = firebaseAuthService.verifierToken(firebaseToken);
            String uid = decodedToken.getUid();
            
            Utilisateur admin = utilisateurRepository.findByFirebaseUid(uid).orElse(null);
            // Seul l'admin principal peut modifier les rôles
            if (admin == null || !"ADMIN_PRINCIPAL".equals(admin.getRole())) {
                response.put("success", false);
                response.put("message", "Seul l'admin principal peut modifier les rôles");
                return ResponseEntity.status(403).body(response);
            }
            
            Utilisateur utilisateur = utilisateurRepository.findById(id).orElse(null);
            if (utilisateur == null) {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Ne pas permettre de modifier son propre rôle
            if (utilisateur.getId().equals(admin.getId())) {
                response.put("success", false);
                response.put("message", "Vous ne pouvez pas modifier votre propre rôle");
                return ResponseEntity.badRequest().body(response);
            }
            
            String nouveauRole = request.get("role");
            if ("ADMIN".equals(nouveauRole) || "CLIENT".equals(nouveauRole)) {
                utilisateur.setRole(nouveauRole);
                utilisateurRepository.save(utilisateur);
                
                response.put("success", true);
                response.put("message", "Rôle modifié avec succès");
                response.put("nouveauRole", nouveauRole);
            } else {
                response.put("success", false);
                response.put("message", "Rôle invalide");
            }

    //NOTIFICATION à l'utilisateur concerné
    Map<String, Object> data = new HashMap<>();
    data.put("nouveauRole", nouveauRole);
    
    if ("ADMIN".equals(nouveauRole)) {
        notificationService.creerNotification(
            utilisateur,
            "👑 Promotion",
            "Vous avez été promu administrateur !",
            "PROMU_ADMIN",
            data
        );
    } else if ("CLIENT".equals(nouveauRole)) {
        notificationService.creerNotification(
            utilisateur,
            "Rétrogradation",
            "Vous avez été rétrogradé client.",
            "RETROGRADE_CLIENT",
            data
        );
    }
    
    // NOTIFICATION à l'admin principal
    Map<String, Object> adminData = new HashMap<>();
    adminData.put("utilisateurNom", utilisateur.getPrenom() + " " + utilisateur.getNom());
    adminData.put("nouveauRole", nouveauRole);
    
    notificationService.creerNotification(
        admin,
        "👑 Changement de rôle",
        "Vous avez " + ("ADMIN".equals(nouveauRole) ? "promu" : "rétrogradé") + " " + utilisateur.getPrenom() + " " + utilisateur.getNom(),
        "CHANGEMENT_ROLE",
        adminData
    );

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // Récupérer le profil de l'utilisateur connecté
@GetMapping("/profil")
public ResponseEntity<Map<String, Object>> getProfil(@RequestHeader("Authorization") String token) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        String firebaseToken = token.substring(7);
        var decodedToken = firebaseAuthService.verifierToken(firebaseToken);
        String uid = decodedToken.getUid();
        
        Utilisateur utilisateur = utilisateurRepository.findByFirebaseUid(uid)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        response.put("success", true);
        response.put("prenom", utilisateur.getPrenom());
        response.put("nom", utilisateur.getNom());
        response.put("adresse", utilisateur.getAdresse());
        response.put("telephone", utilisateur.getTelephone());
        response.put("ville", utilisateur.getVille());
        response.put("codePostal", utilisateur.getCodePostal());
        response.put("pays", utilisateur.getPays());
        
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", e.getMessage());
    }
    
    return ResponseEntity.ok(response);
}

// Modifier le profil de l'utilisateur connecté
@PutMapping("/profil")
public ResponseEntity<Map<String, Object>> updateProfil(@RequestHeader("Authorization") String token,
                                                         @RequestBody Map<String, String> request) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        String firebaseToken = token.substring(7);
        var decodedToken = firebaseAuthService.verifierToken(firebaseToken);
        String uid = decodedToken.getUid();
        
        Utilisateur utilisateur = utilisateurRepository.findByFirebaseUid(uid)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        utilisateur.setPrenom(request.get("prenom"));
        utilisateur.setNom(request.get("nom"));
        utilisateur.setAdresse(request.get("adresse"));
        utilisateur.setTelephone(request.get("telephone"));
        utilisateur.setVille(request.get("ville"));
        utilisateur.setCodePostal(request.get("codePostal"));
        utilisateur.setPays(request.get("pays"));
        
        utilisateurRepository.save(utilisateur);
        
        response.put("success", true);
        response.put("message", "Profil modifié avec succès");
        
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", e.getMessage());
    }
    
    return ResponseEntity.ok(response);
}
}