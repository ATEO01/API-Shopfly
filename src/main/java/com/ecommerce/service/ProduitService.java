package com.ecommerce.service;

import com.ecommerce.dto.ProduitRequest;
import com.ecommerce.dto.ProduitResponse;
import com.ecommerce.model.Produit;
import com.ecommerce.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    
    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }
    
    public ProduitResponse creerProduit(ProduitRequest request) {
        try {
            Produit produit = new Produit();
            produit.setNom(request.getNom());
            produit.setDescription(request.getDescription());
            produit.setPrix(request.getPrix());
            produit.setImageUrl(request.getImageUrl());
            produit.setCategorie(request.getCategorie());
            produit.setUnite(request.getUnite());
            produit.setStock(request.getStock() != null ? request.getStock() : 0);
            
            produitRepository.save(produit);
            
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(true);
            response.setId(produit.getId());
            response.setMessage("Produit créé avec succès");
            response.setImageUrl(produit.getImageUrl());
            return response;
        } catch (Exception e) {
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(false);
            response.setMessage("Erreur: " + e.getMessage());
            return response;
        }
    }
    
    public ProduitResponse modifierProduit(Long id, ProduitRequest request) {
        try {
            Produit produit = produitRepository.findById(id).orElse(null);
            if (produit == null) {
                ProduitResponse response = new ProduitResponse();
                response.setSuccess(false);
                response.setMessage("Produit non trouvé");
                return response;
            }
            
            produit.setNom(request.getNom());
            produit.setDescription(request.getDescription());
            produit.setPrix(request.getPrix());
            if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
                produit.setImageUrl(request.getImageUrl());
            }
            produit.setCategorie(request.getCategorie());
            produit.setUnite(request.getUnite());
            produit.setStock(request.getStock());
            
            produitRepository.save(produit);
            
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(true);
            response.setMessage("Produit modifié avec succès");
            response.setImageUrl(produit.getImageUrl());
            return response;
        } catch (Exception e) {
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(false);
            response.setMessage("Erreur: " + e.getMessage());
            return response;
        }
    }
    
    public ProduitResponse supprimerProduit(Long id) {
        try {
            Produit produit = produitRepository.findById(id).orElse(null);
            if (produit == null) {
                ProduitResponse response = new ProduitResponse();
                response.setSuccess(false);
                response.setMessage("Produit non trouvé");
                return response;
            }
            
            produit.setActif(false);
            produitRepository.save(produit);
            
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(true);
            response.setMessage("Produit supprimé avec succès");
            return response;
        } catch (Exception e) {
            ProduitResponse response = new ProduitResponse();
            response.setSuccess(false);
            response.setMessage("Erreur: " + e.getMessage());
            return response;
        }
    }
    
    public List<ProduitResponse> getAllProduits() {
        return produitRepository.findByActifTrue().stream()
            .map(produit -> {
                ProduitResponse response = new ProduitResponse();
                response.setId(produit.getId());
                response.setNom(produit.getNom());
                response.setDescription(produit.getDescription());
                response.setPrix(produit.getPrix());
                response.setImageUrl(produit.getImageUrl());
                response.setCategorie(produit.getCategorie());
                response.setUnite(produit.getUnite());
                response.setStock(produit.getStock());
                response.setSuccess(true);
                return response;
            })
            .collect(Collectors.toList());
    }
}