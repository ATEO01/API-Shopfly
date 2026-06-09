package com.ecommerce.service;

import com.ecommerce.model.Panier;
import com.ecommerce.model.PanierItem;
import com.ecommerce.model.Produit;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.PanierRepository;
import com.ecommerce.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PanierService {
    
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;
    
    public PanierService(PanierRepository panierRepository, ProduitRepository produitRepository) {
        this.panierRepository = panierRepository;
        this.produitRepository = produitRepository;
    }
    
    public Panier getOrCreatePanier(Utilisateur utilisateur) {
        return panierRepository.findByUtilisateur(utilisateur)
            .orElseGet(() -> {
                Panier panier = new Panier();
                panier.setUtilisateur(utilisateur);
                return panierRepository.save(panier);
            });
    }
    
    @Transactional
    public Map<String, Object> ajouterAuPanier(Utilisateur utilisateur, Long produitId, Integer quantite) {
        Map<String, Object> response = new HashMap<>();
        
        Produit produit = produitRepository.findById(produitId).orElse(null);
        if (produit == null) {
            response.put("success", false);
            response.put("message", "Produit non trouvé");
            return response;
        }
        
        if (produit.getStock() < quantite) {
            response.put("success", false);
            response.put("message", "Stock insuffisant. Maximum: " + produit.getStock());
            return response;
        }
        
        Panier panier = getOrCreatePanier(utilisateur);
        
        // Chercher si le produit est déjà dans le panier
        PanierItem existingItem = null;
        for (PanierItem item : panier.getItems()) {
            if (item.getProduit().getId().equals(produitId)) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            int nouvelleQuantite = existingItem.getQuantite() + quantite;
            if (produit.getStock() < nouvelleQuantite) {
                response.put("success", false);
                response.put("message", "Stock insuffisant");
                return response;
            }
            existingItem.setQuantite(nouvelleQuantite);
        } else {
            PanierItem newItem = new PanierItem();
            newItem.setPanier(panier);
            newItem.setProduit(produit);
            newItem.setQuantite(quantite);
            newItem.setPrix(produit.getPrix());
            panier.getItems().add(newItem);
        }
        
        panierRepository.save(panier);
        
        response.put("success", true);
        response.put("message", "Produit ajouté au panier");
        return response;
    }
    
    @Transactional
    public Map<String, Object> modifierQuantite(Utilisateur utilisateur, Long itemId, Integer quantite) {
        Map<String, Object> response = new HashMap<>();
        
        Panier panier = getOrCreatePanier(utilisateur);
        
        PanierItem item = null;
        for (PanierItem i : panier.getItems()) {
            if (i.getId().equals(itemId)) {
                item = i;
                break;
            }
        }
        
        if (item == null) {
            response.put("success", false);
            response.put("message", "Article non trouvé");
            return response;
        }
        
        Produit produit = item.getProduit();
        
        if (quantite <= 0) {
            panier.getItems().remove(item);
        } else {
            if (produit.getStock() < quantite) {
                response.put("success", false);
                response.put("message", "Stock maximum: " + produit.getStock());
                return response;
            }
            item.setQuantite(quantite);
        }
        
        panierRepository.save(panier);
        
        response.put("success", true);
        return response;
    }
    
    @Transactional
    public Map<String, Object> supprimerArticle(Utilisateur utilisateur, Long itemId) {
        Map<String, Object> response = new HashMap<>();
        
        Panier panier = getOrCreatePanier(utilisateur);
        panier.getItems().removeIf(item -> item.getId().equals(itemId));
        panierRepository.save(panier);
        
        response.put("success", true);
        return response;
    }
    
    public Map<String, Object> getPanier(Utilisateur utilisateur) {
        Panier panier = getOrCreatePanier(utilisateur);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", panier.getId());
        response.put("items", panier.getItems().stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("produitId", item.getProduit().getId());
            itemMap.put("produitNom", item.getProduit().getNom());
            itemMap.put("prix", item.getPrix());
            itemMap.put("quantite", item.getQuantite());
            itemMap.put("sousTotal", item.getPrix() * item.getQuantite());
            return itemMap;
        }).collect(Collectors.toList()));
        response.put("total", panier.getTotal());
        response.put("nombreArticles", panier.getItems().size());
        
        return response;
    }
}