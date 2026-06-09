package com.ecommerce.controller;

import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.UtilisateurRepository;
import com.ecommerce.service.FirebaseAuthService;
import com.ecommerce.service.PanierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client/panier")
public class PanierController {
    
    private final PanierService panierService;
    private final UtilisateurRepository utilisateurRepository;
    private final FirebaseAuthService firebaseAuthService;
    
    public PanierController(PanierService panierService,
                            UtilisateurRepository utilisateurRepository,
                            FirebaseAuthService firebaseAuthService) {
        this.panierService = panierService;
        this.utilisateurRepository = utilisateurRepository;
        this.firebaseAuthService = firebaseAuthService;
    }
    
    private Utilisateur getUtilisateur(String token) {
        String uid = firebaseAuthService.verifierToken(token.substring(7)).getUid();
        return utilisateurRepository.findByFirebaseUid(uid)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPanier(@RequestHeader("Authorization") String token) {
        Utilisateur utilisateur = getUtilisateur(token);
        return ResponseEntity.ok(panierService.getPanier(utilisateur));
    }
    
    @PostMapping("/ajouter")
    public ResponseEntity<Map<String, Object>> ajouterAuPanier(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        Utilisateur utilisateur = getUtilisateur(token);
        Long produitId = ((Number) request.get("produitId")).longValue();
        Integer quantite = (Integer) request.get("quantite");
        return ResponseEntity.ok(panierService.ajouterAuPanier(utilisateur, produitId, quantite));
    }
    
    @PutMapping("/item/{itemId}")
    public ResponseEntity<Map<String, Object>> modifierQuantite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long itemId,
            @RequestParam Integer quantite) {
        Utilisateur utilisateur = getUtilisateur(token);
        return ResponseEntity.ok(panierService.modifierQuantite(utilisateur, itemId, quantite));
    }
    
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Map<String, Object>> supprimerArticle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long itemId) {
        Utilisateur utilisateur = getUtilisateur(token);
        return ResponseEntity.ok(panierService.supprimerArticle(utilisateur, itemId));
    }
}