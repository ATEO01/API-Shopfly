package com.ecommerce.controller.admin;

import com.ecommerce.dto.CommandeResponse;
import com.ecommerce.model.Commande;
import com.ecommerce.model.CommandeItem;
import com.ecommerce.model.Produit;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.ProduitRepository;
import com.ecommerce.service.CommandeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/commandes")
public class CommandeAdminController {
    
    private final CommandeService commandeService;
    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    
    public CommandeAdminController(CommandeService commandeService,
                                   CommandeRepository commandeRepository,
                                   ProduitRepository produitRepository) {
        this.commandeService = commandeService;
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<CommandeResponse>> getAllCommandes() {
        return ResponseEntity.ok(commandeService.getAllCommandes());
    }
    
    @PutMapping("/{id}/statut")
    public ResponseEntity<Map<String, Object>> updateStatut(@PathVariable Long id,
                                                             @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String nouveauStatut = request.get("statut");
        
        String[] statutsValides = {"EN_ATTENTE", "EN_COURS_DE_PREPARATION", "PRET", "EN_COURS_DE_LIVRAISON", "LIVRE", "ANNULEE"};
        boolean valide = false;
        for (String s : statutsValides) {
            if (s.equals(nouveauStatut)) {
                valide = true;
                break;
            }
        }
        
        if (!valide) {
            response.put("success", false);
            response.put("message", "Statut invalide");
            return ResponseEntity.badRequest().body(response);
        }
        
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            response.put("success", false);
            response.put("message", "Commande non trouvée");
            return ResponseEntity.badRequest().body(response);
        }
        
        commande.setStatut(nouveauStatut);
        commandeRepository.save(commande);
        
        response.put("success", true);
        response.put("message", "Statut modifié");
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/pret")
    public ResponseEntity<Map<String, Object>> marquerPret(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            response.put("success", false);
            response.put("message", "Commande non trouvée");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!"EN_COURS_DE_PREPARATION".equals(commande.getStatut())) {
            response.put("success", false);
            response.put("message", "Seules les commandes en préparation peuvent être marquées prêtes");
            return ResponseEntity.badRequest().body(response);
        }
        
        commande.setStatut("PRET");
        commandeRepository.save(commande);
        
        response.put("success", true);
        response.put("message", "Commande marquée comme prête");
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Map<String, Object>> annulerCommande(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            response.put("success", false);
            response.put("message", "Commande non trouvée");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            response.put("success", false);
            response.put("message", "Seules les commandes en attente peuvent être annulées");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Restocker
        for (CommandeItem item : commande.getItems()) {
            if (item.getProduitId() != null) {
                Produit produit = produitRepository.findById(item.getProduitId()).orElse(null);
                if (produit != null) {
                    produit.setStock(produit.getStock() + item.getQuantite());
                    produitRepository.save(produit);
                }
            }
        }
        
        commande.setStatut("ANNULEE");
        commandeRepository.save(commande);
        
        response.put("success", true);
        response.put("message", "Commande annulée et stock restitué");
        
        return ResponseEntity.ok(response);
    }
}