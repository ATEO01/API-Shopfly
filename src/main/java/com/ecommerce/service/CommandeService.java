package com.ecommerce.service;

import com.ecommerce.dto.CommandeRequest;
import com.ecommerce.dto.CommandeResponse;
import com.ecommerce.model.Commande;
import com.ecommerce.model.CommandeItem;
import com.ecommerce.model.Panier;
import com.ecommerce.model.PanierItem;
import com.ecommerce.model.Produit;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.PanierRepository;
import com.ecommerce.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class CommandeService {
    
    private final CommandeRepository commandeRepository;
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;
    
    public CommandeService(CommandeRepository commandeRepository,
                           PanierRepository panierRepository,
                           ProduitRepository produitRepository) {
        this.commandeRepository = commandeRepository;
        this.panierRepository = panierRepository;
        this.produitRepository = produitRepository;
    }
    
    // Récupérer toutes les commandes (admin)
    public List<CommandeResponse> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAllByOrderByDateCommandeDesc();
        return commandes.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    // Récupérer les commandes d'un utilisateur (client)
    public List<CommandeResponse> getCommandesByUtilisateur(Utilisateur utilisateur) {
        List<Commande> commandes = commandeRepository.findByUtilisateurOrderByDateCommandeDesc(utilisateur);
        return commandes.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    // Créer une commande à partir du panier
    @Transactional
    public CommandeResponse creerCommande(Utilisateur utilisateur, CommandeRequest request) {
        Panier panier = panierRepository.findByUtilisateur(utilisateur).orElse(null);
        
        if (panier == null || panier.getItems().isEmpty()) {
            CommandeResponse response = new CommandeResponse();
            response.setSuccess(false);
            response.setMessage("Panier vide");
            return response;
        }
        
        // Créer la commande
        Commande commande = new Commande();
        commande.setUtilisateur(utilisateur);
        commande.setAdresseLivraison(request.getAdresseLivraison());
        commande.setMontantTotal(panier.getTotal());
        commande.setStatut("EN_ATTENTE");
        
        // Ajouter les items
        for (PanierItem panierItem : panier.getItems()) {
            CommandeItem item = new CommandeItem();
            item.setCommande(commande);
            item.setProduitNom(panierItem.getProduit().getNom());
            item.setPrix(panierItem.getPrix());
            item.setQuantite(panierItem.getQuantite());
            item.setSousTotal(panierItem.getPrix() * panierItem.getQuantite());
            commande.getItems().add(item);
            
            // Diminuer le stock
            Produit produit = panierItem.getProduit();
            produit.setStock(produit.getStock() - panierItem.getQuantite());
            produitRepository.save(produit);
        }
        
        commandeRepository.save(commande);
        
        // Vider le panier
        panier.getItems().clear();
        panierRepository.save(panier);
        
        return toResponse(commande);
    }
    
// Mettre à jour le statut d'une commande (admin)
@Transactional
public Map<String, Object> updateStatut(Long commandeId, String nouveauStatut) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        Commande commande = commandeRepository.findById(commandeId).orElse(null);
        if (commande == null) {
            response.put("success", false);
            response.put("message", "Commande non trouvée");
            return response;
        }
        
        String ancienStatut = commande.getStatut();
        commande.setStatut(nouveauStatut);
        commandeRepository.save(commande);
        
        response.put("success", true);
        response.put("message", "Statut modifié avec succès");
        response.put("ancienStatut", ancienStatut);
        response.put("nouveauStatut", nouveauStatut);
        
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "Erreur: " + e.getMessage());
    }
    
    return response;
}
    
    // Annuler une commande (client)
    @Transactional
    public CommandeResponse annulerCommande(Long commandeId, Utilisateur utilisateur) {
        Commande commande = commandeRepository.findById(commandeId).orElse(null);
        
        if (commande == null) {
            CommandeResponse response = new CommandeResponse();
            response.setSuccess(false);
            response.setMessage("Commande non trouvée");
            return response;
        }
        
        if (!commande.getUtilisateur().getId().equals(utilisateur.getId())) {
            CommandeResponse response = new CommandeResponse();
            response.setSuccess(false);
            response.setMessage("Vous n'êtes pas autorisé à annuler cette commande");
            return response;
        }
        
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            CommandeResponse response = new CommandeResponse();
            response.setSuccess(false);
            response.setMessage("Seules les commandes en attente peuvent être annulées");
            return response;
        }
        
        commande.setStatut("ANNULEE");
        commandeRepository.save(commande);
        
        return toResponse(commande);
    }
    
    // Convertir Commande en CommandeResponse
    private CommandeResponse toResponse(Commande commande) {
        CommandeResponse response = new CommandeResponse();
        response.setId(commande.getId());
        response.setUtilisateurId(commande.getUtilisateur().getId());
        response.setUtilisateurNom(commande.getUtilisateur().getPrenom() + " " + commande.getUtilisateur().getNom());
        response.setUtilisateurEmail(commande.getUtilisateur().getEmail());
        response.setMontantTotal(commande.getMontantTotal());
        response.setStatut(commande.getStatut());
        response.setDateCommande(commande.getDateCommande());
        response.setAdresseLivraison(commande.getAdresseLivraison());
        response.setSuccess(true);
        
        List<CommandeResponse.CommandeItemDto> items = commande.getItems().stream().map(item -> {
            CommandeResponse.CommandeItemDto dto = new CommandeResponse.CommandeItemDto();
            dto.setId(item.getId());
            dto.setProduitNom(item.getProduitNom());
            dto.setPrix(item.getPrix());
            dto.setQuantite(item.getQuantite());
            dto.setSousTotal(item.getSousTotal());
            return dto;
        }).collect(Collectors.toList());
        response.setItems(items);
        
        return response;
    }

}