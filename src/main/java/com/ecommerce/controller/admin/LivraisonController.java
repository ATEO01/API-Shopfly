package com.ecommerce.controller.admin;

import com.ecommerce.dto.LivraisonRequest;
import com.ecommerce.dto.LivraisonResponse;
import com.ecommerce.service.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/livraisons")
@RequiredArgsConstructor
public class LivraisonController {
    
    private final LivraisonService livraisonService;
    
    @GetMapping
    public ResponseEntity<List<LivraisonResponse>> getAllLivraisons() {
        return ResponseEntity.ok(livraisonService.getAllLivraisons());
    }
    
    @GetMapping("/commandes-disponibles")
    public ResponseEntity<List<LivraisonResponse.CommandeLivraisonDto>> getCommandesDisponibles() {
        return ResponseEntity.ok(livraisonService.getCommandesDisponibles());
    }
    
    @PostMapping
    public ResponseEntity<LivraisonResponse> creerLivraison(@RequestBody LivraisonRequest request) {
        return ResponseEntity.ok(livraisonService.creerLivraison(request));
    }
    
    @PutMapping("/{id}/statut")
    public ResponseEntity<LivraisonResponse> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(livraisonService.updateStatut(id, statut));
    }
}