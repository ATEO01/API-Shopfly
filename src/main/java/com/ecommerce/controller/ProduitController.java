package com.ecommerce.controller;

import com.ecommerce.dto.ProduitRequest;
import com.ecommerce.dto.ProduitResponse;
import com.ecommerce.service.ProduitService;
import com.ecommerce.service.NotificationService;
import com.ecommerce.service.ImageUploadService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/produits")
public class ProduitController {
    
    private final ProduitService produitService;
    private final ImageUploadService imageUploadService;
    private final NotificationService notificationService;
    
    public ProduitController(ProduitService produitService, ImageUploadService imageUploadService , NotificationService notificationService) {
        this.produitService = produitService;
        this.imageUploadService = imageUploadService;
        this.notificationService = notificationService;
    }
    
    @PostMapping
    public ResponseEntity<ProduitResponse> creerProduit(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") Double prix,
            @RequestParam("categorie") String categorie,
            @RequestParam("unite") String unite,
            @RequestParam("stock") Integer stock,
            @RequestParam("imageFile") MultipartFile imageFile) {
        
        try {
            String imageUrl = imageUploadService.uploadImage(imageFile, nom);
            
            ProduitRequest request = new ProduitRequest();
            request.setNom(nom);
            request.setDescription(description);
            request.setPrix(prix);
            request.setCategorie(categorie);
            request.setUnite(unite);
            request.setStock(stock);
            request.setImageUrl(imageUrl);


            Map<String, Object> data = new HashMap<>();
           // data.put("produitId", request.getId());
            data.put("produitNom", request.getNom());
            data.put("prix", request.getPrix());
    
            notificationService.notifierTousLesClients(
                "🛍️ Nouveau produit disponible !",
                "Le produit " + request.getNom() + " vient d'être ajouté !",
                "NOUVEAU_PRODUIT",
                data);

            
            return ResponseEntity.ok(produitService.creerProduit(request));
        } catch (Exception e) {
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(false);
            response.setMessage("Erreur upload: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponse> modifierProduit(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") Double prix,
            @RequestParam("categorie") String categorie,
            @RequestParam("unite") String unite,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        
        try {
            ProduitRequest request = new ProduitRequest();
            request.setNom(nom);
            request.setDescription(description);
            request.setPrix(prix);
            request.setCategorie(categorie);
            request.setUnite(unite);
            request.setStock(stock);
            
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = imageUploadService.uploadImage(imageFile, nom);
                request.setImageUrl(imageUrl);
            }
            
            return ResponseEntity.ok(produitService.modifierProduit(id, request));
        } catch (Exception e) {
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(false);
            response.setMessage("Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ProduitResponse> supprimerProduit(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.supprimerProduit(id));
    }
    
    @GetMapping
    public ResponseEntity<List<ProduitResponse>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits());
    }
}