package com.ecommerce.controller;

import com.ecommerce.dto.ProfilRequest;
import com.ecommerce.service.FirebaseAuthService;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class UtilisateurController {
    
    private final FirebaseAuthService firebaseAuthService;
    private final UtilisateurRepository utilisateurRepository;
    
    public UtilisateurController(FirebaseAuthService firebaseAuthService,
                                 UtilisateurRepository utilisateurRepository) {
        this.firebaseAuthService = firebaseAuthService;
        this.utilisateurRepository = utilisateurRepository;
    }
    
    @PutMapping("/completer-profil")
    public ResponseEntity<Map<String, Object>> completerProfil(
            @RequestHeader("Authorization") String token,
            @RequestBody ProfilRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String firebaseToken = token.substring(7);
            var decodedToken = firebaseAuthService.verifierToken(firebaseToken);
            String uid = decodedToken.getUid();
            
            Utilisateur utilisateur = utilisateurRepository.findByFirebaseUid(uid)
                .orElse(null);
            
            if (utilisateur == null) {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getTelephone() != null) utilisateur.setTelephone(request.getTelephone());
            if (request.getAdresse() != null) utilisateur.setAdresse(request.getAdresse());
            if (request.getVille() != null) utilisateur.setVille(request.getVille());
            if (request.getCodePostal() != null) utilisateur.setCodePostal(request.getCodePostal());
            if (request.getPays() != null) utilisateur.setPays(request.getPays());
            
            utilisateurRepository.save(utilisateur);
            
            response.put("success", true);
            response.put("message", "Profil mis à jour");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}