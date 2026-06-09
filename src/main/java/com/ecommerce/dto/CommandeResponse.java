package com.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeResponse {
    private Long id;
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurEmail;
    private Double montantTotal;
    private String statut;
    private LocalDateTime dateCommande;
    private String adresseLivraison;
    private List<CommandeItemDto> items;
    private boolean success;
    private String message;
    
    // Inner class pour les items
    public static class CommandeItemDto {
        private Long id;
        private String produitNom;
        private Double prix;
        private Integer quantite;
        private Double sousTotal;
        
        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getProduitNom() { return produitNom; }
        public void setProduitNom(String produitNom) { this.produitNom = produitNom; }
        public Double getPrix() { return prix; }
        public void setPrix(Double prix) { this.prix = prix; }
        public Integer getQuantite() { return quantite; }
        public void setQuantite(Integer quantite) { this.quantite = quantite; }
        public Double getSousTotal() { return sousTotal; }
        public void setSousTotal(Double sousTotal) { this.sousTotal = sousTotal; }
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }
    public String getUtilisateurEmail() { return utilisateurEmail; }
    public void setUtilisateurEmail(String utilisateurEmail) { this.utilisateurEmail = utilisateurEmail; }
    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    public List<CommandeItemDto> getItems() { return items; }
    public void setItems(List<CommandeItemDto> items) { this.items = items; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}